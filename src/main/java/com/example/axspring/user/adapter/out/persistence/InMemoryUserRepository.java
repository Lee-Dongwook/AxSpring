package com.example.axspring.user.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

/**
 * 개발 단계에서 사용할 사용자 저장소 구현입니다.
 * 애플리케이션을 재시작하면 저장된 데이터는 초기화됩니다.
 */
@Repository
@Profile("in-memory")
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public boolean existsByEmail(Email email) {
        return usersByEmail.containsKey(email.value());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return Optional.ofNullable(usersByEmail.get(email.value()));
    }

    @Override
    public User save(User user) {
        usersByEmail.put(user.email().value(), user);
        return user;
    }
}
