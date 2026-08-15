package com.example.axspring.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class User {

    // Identity
    private final UserId id;
    private String name;
    private Email email;
    private Instant emailVerifiedAt;
    private String imageUrl;

    // Authentication / Authorization
    private String passwordHash;
    private UserRole role;

    // Organization
    private String department;
    private String position;
    private LocalDate hireDate;
    private LocalDate birthDate;

    // External accounts
    private String slackUserId;
    private String googleAccountId;
    private String notionPersonId;
    private String linearUserId;
    private String githubLogin;
    private List<Email> emailAliases;

    // Account state
    private boolean active;
    private boolean mustChangePassword;
    private Instant passwordChangedAt;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            UserId id,
            String name,
            Email email,
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
            List<Email> emailAliases,
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
        this.emailAliases = emailAliases == null
                ? List.of()
                : List.copyOf(emailAliases);

        this.active = active;
        this.mustChangePassword = mustChangePassword;
        this.passwordChangedAt = passwordChangedAt;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User register(
            UserId id,
            String name,
            Email email,
            String passwordHash,
            Instant now
    ) {
        return new User(
                id,
                name,
                email,
                null,
                null,
                passwordHash,
                UserRole.MEMBER,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(),
                true,
                true,
                null,
                now,
                now
        );
    }

    public void verifyEmail(Instant now) {
        this.emailVerifiedAt = now;
        this.updatedAt = now;
    }

    public void changePassword(
            String newPasswordHash,
            Instant now
    ) {
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = now;
        this.mustChangePassword = false;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public void updateProfile(
            String name,
            String imageUrl,
            String department,
            String position,
            LocalDate hireDate,
            LocalDate birthDate,
            Instant now
    ) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.birthDate = birthDate;
        this.updatedAt = now;
    }

    public void connectSlack(
            String slackUserId,
            Instant now
    ) {
        this.slackUserId = slackUserId;
        this.updatedAt = now;
    }

    public void connectGoogle(
            String googleAccountId,
            Instant now
    ) {
        this.googleAccountId = googleAccountId;
        this.updatedAt = now;
    }

    public void connectNotion(
            String notionPersonId,
            Instant now
    ) {
        this.notionPersonId = notionPersonId;
        this.updatedAt = now;
    }

    public void connectLinear(
            String linearUserId,
            Instant now
    ) {
        this.linearUserId = linearUserId;
        this.updatedAt = now;
    }

    public void connectGithub(
            String githubLogin,
            Instant now
    ) {
        this.githubLogin = githubLogin;
        this.updatedAt = now;
    }

    public void updateEmailAliases(
            List<Email> emailAliases,
            Instant now
    ) {
        this.emailAliases = List.copyOf(emailAliases);
        this.updatedAt = now;
    }

    public UserId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public Instant emailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserRole role() {
        return role;
    }

    public String department() {
        return department;
    }

    public String position() {
        return position;
    }

    public LocalDate hireDate() {
        return hireDate;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String slackUserId() {
        return slackUserId;
    }

    public String googleAccountId() {
        return googleAccountId;
    }

    public String notionPersonId() {
        return notionPersonId;
    }

    public String linearUserId() {
        return linearUserId;
    }

    public String githubLogin() {
        return githubLogin;
    }

    public List<Email> emailAliases() {
        return emailAliases;
    }

    public boolean active() {
        return active;
    }

    public boolean mustChangePassword() {
        return mustChangePassword;
    }

    public Instant passwordChangedAt() {
        return passwordChangedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
