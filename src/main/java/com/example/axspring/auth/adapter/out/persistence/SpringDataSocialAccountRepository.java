package com.example.axspring.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.axspring.auth.domain.AuthProvider;

public interface SpringDataSocialAccountRepository
        extends JpaRepository<SocialAccountJpaEntity, Long> {

    Optional<SocialAccountJpaEntity>
    findByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );

    Optional<SocialAccountJpaEntity>
    findByUserIdAndProvider(
            String userId,
            AuthProvider provider
    );
}
