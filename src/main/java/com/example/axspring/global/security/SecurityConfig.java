package com.example.axspring.global.security;

import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.axspring.auth.adapter.out.token.JwtProperties;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/health",
                        "/api/users",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter)));
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder(
        RSAPublicKey publicKey,
        JwtProperties properties
    ) {
        NimbusJwtDecoder decoder = 
                NimbusJwtDecoder.withPublicKey(publicKey).build();

        var issueValidator = 
            JwtValidators.createDefaultWithIssuer(
                properties.issuer()
                );
        
        var audienceValidator = 
                new JwtAudienceValidator(
                    properties.audience()
                );
        
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<Jwt>(
                issueValidator,
                audienceValidator
            )
        );

        return decoder;
    }
}
