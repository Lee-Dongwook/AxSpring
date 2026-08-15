package com.example.axspring.user.domain;

import java.time.Instant;
import java.time.LocalDate;

public class User {

    // Identity
    private final UserId id;

    private String name;
    private Email email;
    private String imageUrl;

    private UserRole role;

    // Organization
    private String department;
    private String position;

    private LocalDate hireDate;
    private LocalDate birthDate;

    // Account state
    private boolean active;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            UserId id,
            String name,
            Email email,
            String imageUrl,
            UserRole role,
            String department,
            String position,
            LocalDate hireDate,
            LocalDate birthDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.birthDate = birthDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User register(
            UserId id,
            String name,
            Email email,
            Instant now
    ) {
        return new User(
                id,
                name,
                email,
                null,
                UserRole.MEMBER,
                null,
                null,
                null,
                null,
                true,
                now,
                now
        );
    }

    public static User restore(
            UserId id,
            String name,
            Email email,
            String imageUrl,
            UserRole role,
            String department,
            String position,
            LocalDate hireDate,
            LocalDate birthDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new User(
                id,
                name,
                email,
                imageUrl,
                role,
                department,
                position,
                hireDate,
                birthDate,
                active,
                createdAt,
                updatedAt
        );
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

    public UserId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public String imageUrl() {
        return imageUrl;
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

    public boolean active() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
