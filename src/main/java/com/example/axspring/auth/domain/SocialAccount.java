package com.example.axspring.auth.domain;

import java.time.Instant;

import com.example.axspring.user.domain.UserId;

public class SocialAccount {
    private final UserId userId;
    private final AuthProvider provider;
    private final String providerUserId;

    private String email;

    private final Instant createdAt;
    private Instant updatedAt;

    private SocialAccount(
            UserId userId,
            AuthProvider provider,
            String providerUserId,
            String email,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.userId = userId;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SocialAccount create(
        UserId userId,
        AuthProvider provider,
        String providerUserId,
        String email,
        Instant now
    ) {
        return new SocialAccount(
            userId,
            provider,
            providerUserId,
            email,
            now,
            now
        );
    }

    public static SocialAccount restore(
        UserId userId,
        AuthProvider provider,
        String providerUserId,
        String email,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new SocialAccount(
            userId,
            provider,
            providerUserId,
            email,
            createdAt,
            updatedAt
        )
    }

    public UserId userId() {
        return userId;
    }

    public AuthProvider provider() {
        return provider;
    }

    public String providerUserId() {
        return providerUserId;
    }

    public String email() {
        return email;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
