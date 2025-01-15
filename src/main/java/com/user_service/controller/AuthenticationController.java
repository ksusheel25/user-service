package com.user_service.controller;

import com.user_service.dtos.LoginResponse;
import com.user_service.dtos.LoginUserDto;
import com.user_service.dtos.RegisterUserDto;
import com.user_service.model.User;
import com.user_service.service.AuthenticationService;
import com.user_service.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, ClientRegistrationRepository clientRegistrationRepository, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld(){
        return new ResponseEntity<>("Hello Everyone", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse =LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/google")
    public RedirectView googleAuth(HttpServletRequest request) {
        ClientRegistration googleClient = clientRegistrationRepository.findByRegistrationId("google");
        String redirectUri = UriComponentsBuilder.fromUriString(googleClient.getProviderDetails().getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", googleClient.getClientId())
                .queryParam("redirect_uri", googleClient.getRedirectUri())  // Match this with your console
                .queryParam("scope", String.join(" ", googleClient.getScopes()))
                .toUriString();

        return new RedirectView(redirectUri);
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("code") String code) {
        // Use the code to request the access token and fetch user info
        // Implement your logic here to handle the user info
        return ResponseEntity.ok(code);
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> oauth2Success() {
        return ResponseEntity.ok("Google authentication successful!");
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google authentication failed!");
    }
}
