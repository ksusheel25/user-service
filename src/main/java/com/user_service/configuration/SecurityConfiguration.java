package com.user_service.configuration;

import com.user_service.service.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->{
                    cors.configurationSource(new CorsConfigurationSource() {

                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                            CorsConfiguration cfg= new CorsConfiguration();
                            cfg.setAllowedOriginPatterns(Collections.singletonList("*"));
                            cfg.setAllowedMethods(Collections.singletonList("*"));
                            cfg.setAllowCredentials(true);
                            cfg.setAllowedHeaders(Collections.singletonList("*"));
                            cfg.setExposedHeaders(List.of("Authorization", "Content-Type"));
                            return cfg;

                        }
                    });
                })

                .authorizeHttpRequests(auth ->{
                    auth
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers("/swagger-ui*/**","/v3/api-docs/**").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .oauth2Login((login)-> login.loginPage("/auth/google")
                        .defaultSuccessUrl("/auth/oauth2/success") // Redirect after successful login
                        .failureUrl("/auth/oauth2/failure"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
