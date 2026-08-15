package com.example.axspring.auth.adapter.out.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_credentials")
public class UserCredentialJpaEntity {

    @Id
    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    protected UserCredentialJpaEntity() {
    }

    public UserCredentialJpaEntity(
            String userId,
            String passwordHash,
            boolean mustChangePassword,
            Instant passwordChangedAt
    ) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.mustChangePassword = mustChangePassword;
        this.passwordChangedAt = passwordChangedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public Instant getPasswordChangedAt() {
        return passwordChangedAt;
    }
}
