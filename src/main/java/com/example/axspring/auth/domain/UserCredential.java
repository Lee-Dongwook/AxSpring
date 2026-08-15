package com.example.axspring.auth.domain;

import java.time.Instant;

import com.example.axspring.user.domain.UserId;

public class UserCredential {

    private final UserId userId;

    private String passwordHash;
    private boolean mustChangePassword;
    private Instant passwordChangedAt;

    private UserCredential(
            UserId userId,
            String passwordHash,
            boolean mustChangePassword,
            Instant passwordChangedAt
    ) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.mustChangePassword = mustChangePassword;
        this.passwordChangedAt = passwordChangedAt;
    }

    public static UserCredential create(
            UserId userId,
            String passwordHash
    ) {
        return new UserCredential(
                userId,
                passwordHash,
                true,
                null
        );
    }

    public static UserCredential restore(
            UserId userId,
            String passwordHash,
            boolean mustChangePassword,
            Instant passwordChangedAt
    ) {
        return new UserCredential(
                userId,
                passwordHash,
                mustChangePassword,
                passwordChangedAt
        );
    }

    public void changePassword(
            String newPasswordHash,
            Instant now
    ) {
        this.passwordHash = newPasswordHash;
        this.mustChangePassword = false;
        this.passwordChangedAt = now;
    }

    public UserId userId() {
        return userId;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public boolean mustChangePassword() {
        return mustChangePassword;
    }

    public Instant passwordChangedAt() {
        return passwordChangedAt;
    }
}
