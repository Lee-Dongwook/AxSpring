package com.example.axspring.user.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, String> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
}
