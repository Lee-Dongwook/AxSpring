package com.example.axspring.user.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
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

    @Column(name = "image_url")
    private String imageUrl;

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

    @Column(name = "is_active", nullable = false)
    private boolean active;

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
