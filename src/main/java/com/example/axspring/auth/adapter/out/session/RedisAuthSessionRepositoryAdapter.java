package com.example.axspring.auth.adapter.out.session;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.axspring.auth.application.port.out.AuthSessionRepository;
import com.example.axspring.auth.domain.AuthSession;
import com.example.axspring.auth.domain.SessionId;

@Repository
public class RedisAuthSessionRepositoryAdapter implements AuthSessionRepository {
    private static final String KEY_PREFIX = "auth:session";

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, Object, Object> hashOperations;
    
    public RedisAuthSessionRepositoryAdapter(
        RedisTemplate<String, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Optional<AuthSession> findById(
            SessionId sessionId
    ) {
        String key = key(sessionId);

        Map<Object, Object> values =
                hashOperations.entries(key);

        if (values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                AuthSessionRedisMapper.toDomain(
                        sessionId,
                        values
                )
        );
    }

    @Override
    public AuthSession save(
        AuthSession session
    ) {
        String key = key(session.id());

        Map<String, String> values = 
            AuthSessionRedisMapper.toHash(session);

        hashOperations.putAll(key, values);

        Duration ttl = Duration.between(
            Instant.now(),
            session.expiresAt()
        );

        if(ttl.isNegative() || ttl.isZero()) {
            redisTemplate.delete(key);
            return session;
        }

        redisTemplate.expire(key, ttl);

        return session;
    }

    @Override
    public void deleteById(
            SessionId sessionId
    ) {
        redisTemplate.delete(key(sessionId));
    }

    private String key(SessionId sessionId) {
        return KEY_PREFIX + sessionId.value();
    }
}
