package com.user_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Arrays;

@Configuration
public class OAuth2Config {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookClientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookClientSecret;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String facebookRedirectUri;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String githubRedirectUri;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleClient = ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .redirectUri(googleRedirectUri)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        ClientRegistration facebookClient = ClientRegistration.withRegistrationId("facebook")
                .clientId(facebookClientId)
                .clientSecret(facebookClientSecret)
                .scope("email", "public_profile")
                .authorizationUri("https://www.facebook.com/v12.0/dialog/oauth")
                .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
                .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
                .userNameAttributeName("id")
                .redirectUri(facebookRedirectUri)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        ClientRegistration githubClient = ClientRegistration.withRegistrationId("github")
                .clientId(githubClientId)
                .clientSecret(githubClientSecret)
                .scope("read:user", "user:email")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .redirectUri(githubRedirectUri)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        return new InMemoryClientRegistrationRepository(Arrays.asList(googleClient, facebookClient, githubClient));
    }
}
