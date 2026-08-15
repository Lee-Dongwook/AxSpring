package com.example.axspring.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, String> {
    boolean existsByEmailIgnoreCase(String email);
}

