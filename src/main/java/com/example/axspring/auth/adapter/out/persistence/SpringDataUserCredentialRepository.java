package com.example.axspring.auth.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserCredentialRepository
        extends JpaRepository<UserCredentialJpaEntity, String> {
}
