package com.example.axspring.auth.domain;

import java.time.Instant;

import com.example.axspring.user.domain.UserId;

public class AuthSession {
    private final SessionId id;
    private final UserId userId;
    
    private String refreshTokenHash;

    private final Instant createdAt;
    private Instant lastUsedAt;
    private final Instant expiresAt;

    private String userAgent;
    private String ipAddress;

    private AuthSession(
        SessionId id,
        UserId userId,
        String refreshTokenHash,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt,
        String userAgent,
        String ipAddress
    ) {
        this.id = id;
        this.userId = userId;
        this.refreshTokenHash = refreshTokenHash;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    public static AuthSession create(
            SessionId id,
            UserId userId,
            String refreshTokenHash,
            Instant now,
            Instant expiresAt,
            String userAgent,
            String ipAddress
    ) {
        return new AuthSession(
            id,
            userId,
            refreshTokenHash,
            now,
            now,
            expiresAt,
            userAgent,
            ipAddress
        );
    }

    public static AuthSession restore(
            SessionId id,
            UserId userId,
            String refreshTokenHash,
            Instant createdAt,
            Instant lastUsedAt,
            Instant expiresAt,
            String userAgent,
            String ipAddress
    ) {
        return new AuthSession(
            id,
            userId,
            refreshTokenHash,
            createdAt,
            lastUsedAt,
            expiresAt,
            userAgent,
            ipAddress
        );
    }

    public void rotateRefreshToken(
        String newRefreshTokenHash,
        Instant now
    ) {
        this.refreshTokenHash = newRefreshTokenHash;
        this.lastUsedAt = now;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public SessionId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public String refreshTokenHash() {
        return refreshTokenHash;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant lastUsedAt() {
        return lastUsedAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

     public String userAgent() {
        return userAgent;
    }

    public String ipAddress() {
        return ipAddress;
    }
}
