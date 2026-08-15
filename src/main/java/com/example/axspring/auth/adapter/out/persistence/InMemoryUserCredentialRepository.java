package com.example.axspring.auth.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.auth.application.port.out.UserCredentialRepository;
import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.domain.UserId;

/**
 * 개발 단계에서 사용할 인증 정보 저장소 구현입니다.
 * 애플리케이션을 재시작하면 저장된 데이터는 초기화됩니다.
 */
@Repository
@Profile("in-memory")
public class InMemoryUserCredentialRepository
        implements UserCredentialRepository {

    private final Map<String, UserCredential> credentialsByUserId =
            new ConcurrentHashMap<>();

    @Override
    public UserCredential save(UserCredential credential) {
        credentialsByUserId.put(credential.userId().value(), credential);
        return credential;
    }

    @Override
    public Optional<UserCredential> findByUserId(UserId userId) {
        return Optional.ofNullable(credentialsByUserId.get(userId.value()));
    }
}
