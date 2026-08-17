package com.example.axspring.integration.domain;

import java.time.Instant;

import com.example.axspring.user.domain.UserId;

public class IntegrationToken {
    private final String id;
    private final UserId userId;
    private final IntegrationProvider provider;


    private String encryptedAccessToken;
    private String encryptedRefreshToken;

    private Instant expiresAt;

    private String scope;
    private String tokenType;
    private String accountEmail;

    private Instant revokedAt;

    private final Instant createdAt;
    private Instant updatedAt;

    private IntegrationToken(
            String id,
            UserId userId,
            IntegrationProvider provider,
            String encryptedAccessToken,
            String encryptedRefreshToken,
            Instant expiresAt,
            String scope,
            String tokenType,
            String accountEmail,
            Instant revokedAt,
            Instant createdAt,
            Instant updatedAt
     ) {
         this.id = id;
         this.userId = userId;
         this.provider = provider;
         this.encryptedAccessToken = encryptedAccessToken;
         this.encryptedRefreshToken = encryptedRefreshToken;
         this.expiresAt = expiresAt;
         this.scope = scope;
         this.tokenType = tokenType;
         this.accountEmail = accountEmail;
         this.revokedAt = revokedAt;
         this.createdAt = createdAt;
         this.updatedAt = updatedAt;
     }
    
    public static IntegrationToken connect(
            String id,
            UserId userId,
            IntegrationProvider provider,
            String encryptedAccessToken,
            String encryptedRefreshToken,
            Instant expiresAt,
            String scope,
            String tokenType,
            String accountEmail,
            Instant now
    ) {
        return new IntegrationToken(
                id,
                userId,
                provider,
                encryptedAccessToken,
                encryptedRefreshToken,
                expiresAt,
                scope,
                tokenType,
                accountEmail,
                null,
                now,
                now);
    }
    

    public static IntegrationToken restore(
            String id,
            UserId userId,
            IntegrationProvider provider,
            String encryptedAccessToken,
            String encryptedRefreshToken,
            Instant expiresAt,
            String scope,
            String tokenType,
            String accountEmail,
            Instant revokedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new IntegrationToken(
                id,
                userId,
                provider,
                encryptedAccessToken,
                encryptedRefreshToken,
                expiresAt,
                scope,
                tokenType,
                accountEmail,
                revokedAt,
                createdAt,
                updatedAt);
    }
    
    public void refresh(
            String encryptedAccessToken,
            String encryptedRefreshToken,
            Instant expiresAt,
            Instant now
    ) {
        this.encryptedAccessToken = encryptedAccessToken;

        if (encryptedRefreshToken != null) {
            this.encryptedRefreshToken = encryptedRefreshToken;
        }

        this.expiresAt = expiresAt;
        this.updatedAt = now;
    }

    public void revoke(Instant now) {
        this.revokedAt = now;
        this.updatedAt = now;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public String id() { return id; }
    public UserId userId() { return userId; }
    public IntegrationProvider provider() { return provider; }
    public String encryptedAccessToken() { return encryptedAccessToken; }
    public String encryptedRefreshToken() { return encryptedRefreshToken; }
    public Instant expiresAt() { return expiresAt; }
    public String scope() { return scope; }
    public String tokenType() { return tokenType; }
    public String accountEmail() { return accountEmail; }
    public Instant revokedAt() { return revokedAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
