package com.example.axspring.auth.adapter.out.token;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.axspring.auth.application.port.out.TokenIssuer;
import com.example.axspring.auth.domain.SessionId;
import com.example.axspring.user.domain.UserId;
import com.example.axspring.user.domain.UserRole;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtTokenAdapter implements TokenIssuer {
    private final JwtProperties properties;
    private final RSASSASigner signer;

    public JwtTokenAdapter(
        JwtProperties properties,
        RSASSASigner signer
    ) {
        this.properties = properties;
        this.signer = signer;
    }

    @Override
    public String issueAccessToken(
        UserId userId,
        SessionId sessionId,
        UserRole role
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(
            properties.accessTokenTtl()
        );

        JWSHeader header = new JWSHeader.Builder(
            JWSAlgorithm.RS256
        ).type(JOSEObjectType.JWT)
        .keyID(properties.keyId())
        .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .subject(userId.value())
                .audience(properties.audience())
                .claim("sid", sessionId.value())
                .claim("role", role.name())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiresAt))
                .build();
        
        SignedJWT jwt = new SignedJWT(header, claims);

        try {
            jwt.sign(signer);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to sign access token",
                e
            );
        }
        
        return jwt.serialize();
    }
}
