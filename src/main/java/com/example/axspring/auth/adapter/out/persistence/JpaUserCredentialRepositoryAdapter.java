package com.example.axspring.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.auth.application.port.out.UserCredentialRepository;
import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.domain.UserId;

@Repository
@Profile("!in-memory")
public class JpaUserCredentialRepositoryAdapter
        implements UserCredentialRepository {

    private final SpringDataUserCredentialRepository repository;

    public JpaUserCredentialRepositoryAdapter(
            SpringDataUserCredentialRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public UserCredential save(UserCredential credential) {
        UserCredentialJpaEntity entity =
                UserCredentialPersistenceMapper.toEntity(credential);

        UserCredentialJpaEntity saved =
                repository.save(entity);

        return UserCredentialPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<UserCredential> findByUserId(UserId userId) {
        return repository
                .findById(userId.value())
                .map(UserCredentialPersistenceMapper::toDomain);
    }
}
