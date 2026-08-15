package com.example.axspring.auth.application.port.out;

import java.util.Optional;

import com.example.axspring.auth.domain.AuthSession;
import com.example.axspring.auth.domain.SessionId;

public interface AuthSessionRepository {

    Optional<AuthSession> findById(SessionId sessionId);

    AuthSession save(AuthSession session);

    void deleteById(SessionId sessionId);
}
