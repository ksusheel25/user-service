package com.user_service.controller;

import com.user_service.dtos.LoginResponse;
import com.user_service.dtos.LoginUserDto;
import com.user_service.dtos.RegisterUserDto;
import com.user_service.emailService.EmailService;
import com.user_service.model.User;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import com.user_service.service.AuthenticationService;
import com.user_service.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository; // Add this
    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;

    public AuthenticationController(JwtService jwtService, ClientRegistrationRepository clientRegistrationRepository,
                                    AuthenticationService authenticationService,
                                    UserRepository userRepository, EmailService emailService) {
        this.jwtService = jwtService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {

        User registeredUser = authenticationService.signup(registerUserDto);

        String token = UUID.randomUUID().toString();
        registeredUser.setVerificationToken(token);
        userRepository.save(registeredUser);

        String verificationLink = "http://localhost:8080/auth/verify-email?token=" + token;
        emailService.sendVerificationEmail(registeredUser.getEmail(), verificationLink);
        return ResponseEntity.ok("User registered successfully. Please verify your email");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user != null) {
            user.setEmailVerified(true);
            user.setVerificationToken(null); // Clear the token
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully!, Please login to your account.");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello Everyone");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String verificationToken = authenticatedUser.getVerificationToken();
        if(!authenticatedUser.isEmailVerified()) {
            String verificationLink = "http://localhost:8080/auth/verify-email?token=" + verificationToken;
            emailService.sendVerificationEmail(authenticatedUser.getEmail(), verificationLink);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email not verified. A new verification link has been sent to your email.");
        }
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();
        return ResponseEntity.ok(loginResponse);
    }

    // OAuth2 Login url like "/auth2/login?provider=google"
    @GetMapping("/auth2/login")
    public RedirectView oauth2Login(@RequestParam(required = false) String provider) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        log.info("inside oauth2Login");
        String redirectUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .queryParam("scope", String.join(" ", clientRegistration.getScopes()))
                .toUriString();
        return new RedirectView(redirectUri);
    }

    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<?> handleOAuth2Callback(@PathVariable String provider, @RequestParam("code") String code) {
        return handleProviderCallback(code, provider);
    }

    private ResponseEntity<?> handleProviderCallback(String code, String provider) {
        log.info("Provider is {}", provider);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        log.info("provider registration id {}", clientRegistration.getRegistrationId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("client_id", clientRegistration.getClientId());
        bodyParams.add("client_secret", clientRegistration.getClientSecret());
        bodyParams.add("code", code);
        bodyParams.add("redirect_uri", clientRegistration.getRedirectUri());
        bodyParams.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParams, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(clientRegistration.getProviderDetails().getTokenUri(), request, Map.class);

        if (tokenResponse.getBody() == null || !tokenResponse.getBody().containsKey("access_token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve access token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        if ("facebook".equals(provider)) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(userInfoUri)
                    .queryParam("fields", "id,name,email")
                    .queryParam("access_token", accessToken);
            userInfoUri = builder.toUriString();
            userInfoRequest = new HttpEntity<>(headers);
        }

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoRequest, Map.class);

        if (userInfoResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user information");
        }

        String email = (String) userInfoResponse.getBody().get("email");
        String name = (String) userInfoResponse.getBody().get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    RegisterUserDto registerUserDto = new RegisterUserDto();
                    registerUserDto.setFullName(name);
                    registerUserDto.setEmail(email);
                    registerUserDto.setPassword("");
                    return authenticationService.signup(registerUserDto);
                });
        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(LoginResponse.builder().token(jwt).expiresIn(jwtService.getExpirationTime()).build());
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> oauth2Success() {
        return ResponseEntity.ok("OAuth2 authentication successful!");
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString(); // Generate unique reset token
        user.setResetToken(resetToken);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);

        // Send reset link via email
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + resetToken;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear the reset token after a successful password change
        user.setTokenExpiration(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been successfully reset.");
    }

}
