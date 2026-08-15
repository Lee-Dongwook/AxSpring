package com.example.axspring.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.auth.application.port.out.SocialAccountRepository;
import com.example.axspring.auth.domain.AuthProvider;
import com.example.axspring.auth.domain.SocialAccount;
import com.example.axspring.user.domain.UserId;

@Repository
@Profile("!in-memory")
public class JpaSocialAccountRepositoryAdapter implements SocialAccountRepository {
    private final SpringDataSocialAccountRepository repository;

    public JpaSocialAccountRepositoryAdapter(
            SpringDataSocialAccountRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<SocialAccount> findByProviderAndProviderUserId(
        AuthProvider provider,
        String providerUserId
    ) {
        return repository
                .findByProviderAndProviderUserId(
                        provider,
                        providerUserId
                )
                .map(SocialAccountPersistenceMapper::toDomain);
    }

    @Override
    public Optional<SocialAccount> findByUserIdAndProvider(
            UserId userId,
            AuthProvider provider
    ) {
        return repository
                .findByUserIdAndProvider(
                        userId.value(),
                        provider
                )
                .map(SocialAccountPersistenceMapper::toDomain);
    }

    @Override
    public SocialAccount save(
            SocialAccount socialAccount
    ) {
        SocialAccountJpaEntity entity =
                SocialAccountPersistenceMapper.toEntity(
                        socialAccount
                );

        SocialAccountJpaEntity saved =
                repository.save(entity);

        return SocialAccountPersistenceMapper.toDomain(saved);
    }
}
