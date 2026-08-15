package com.example.axspring.user.adapter.out.persistence;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

@Repository
@Profile("!in-memory")
public class JpaUserRepositoryAdapter implements UserRepository {
    private final SpringDataUserRepository repository;

     public JpaUserRepositoryAdapter(
            SpringDataUserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmailIgnoreCase(email.value());
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity =
                UserPersistenceMapper.toEntity(user);
        
        UserJpaEntity saved =
                repository.save(entity);
        
        return UserPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository
                    .findByEmailIgnoreCase(email.value())
                    .map(UserPersistenceMapper::toDomain);
    }
}
