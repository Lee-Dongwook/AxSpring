package com.example.axspring.auth.adapter.out.token;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        String audience,
        String keyId,
        String privateKeyPath,
        String publicKeyPath,
        Duration accessTokenTtl,
        Duration refreshTokenTtl
) {
}
