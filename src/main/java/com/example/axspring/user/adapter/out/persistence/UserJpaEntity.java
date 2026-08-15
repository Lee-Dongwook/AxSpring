package com.example.axspring.user.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.axspring.user.domain.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 320, nullable = false)
    private String email;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "image_url")
    private String imageUrl;

     @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 32, nullable = false)
    private UserRole role;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "slack_user_id", length = 100)
    private String slackUserId;

    @Column(name = "google_account_id", length = 255)
    private String googleAccountId;

    @Column(name = "notion_person_id", length = 255)
    private String notionPersonId;

    @Column(name = "linear_user_id", length = 255)
    private String linearUserId;

    @Column(name = "github_login", length = 255)
    private String githubLogin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
        name = "email_aliases",
        columnDefinition = "jsonb",
        nullable = false
    )
    private List<String> emailAliases;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * JPA가 Entity를 생성할 때 필요.
     * 애플리케이션에서 직접 사용하는 생성자가 아니므로 protected.
     */
    protected UserJpaEntity() {

    }

    public UserJpaEntity(
            String id,
            String name,
            String email,
            Instant emailVerifiedAt,
            String imageUrl,
            String passwordHash,
            UserRole role,
            String department,
            String position,
            LocalDate hireDate,
            LocalDate birthDate,
            String slackUserId,
            String googleAccountId,
            String notionPersonId,
            String linearUserId,
            String githubLogin,
            List<String> emailAliases,
            boolean active,
            boolean mustChangePassword,
            Instant passwordChangedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.emailVerifiedAt = emailVerifiedAt;
        this.imageUrl = imageUrl;
        this.passwordHash = passwordHash;
        this.role = role;

        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.birthDate = birthDate;

        this.slackUserId = slackUserId;
        this.googleAccountId = googleAccountId;
        this.notionPersonId = notionPersonId;
        this.linearUserId = linearUserId;
        this.githubLogin = githubLogin;
        this.emailAliases = emailAliases;

        this.active = active;
        this.mustChangePassword = mustChangePassword;
        this.passwordChangedAt = passwordChangedAt;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt; 
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Instant getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getSlackUserId() {
        return slackUserId;
    }

    public String getGoogleAccountId() {
        return googleAccountId;
    }

    public String getNotionPersonId() {
        return notionPersonId;
    }

    public String getLinearUserId() {
        return linearUserId;
    }

     public String getGithubLogin() {
        return githubLogin;
    }

    public List<String> getEmailAliases() {
        return emailAliases;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public Instant getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
