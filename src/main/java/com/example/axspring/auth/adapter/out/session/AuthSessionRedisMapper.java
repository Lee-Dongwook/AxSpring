package com.example.axspring.auth.adapter.out.session;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.example.axspring.auth.domain.AuthSession;
import com.example.axspring.auth.domain.SessionId;
import com.example.axspring.user.domain.UserId;

public final class AuthSessionRedisMapper {
    private AuthSessionRedisMapper() {

    }    

    public static Map<String, String> toHash(
        AuthSession session
    ) {
        Map<String, String> values = new HashMap<>();

        values.put("userId", session.userId().value());

        values.put("refreshTokenHash", session.refreshTokenHash());

        values.put("createdAt", session.createdAt().toString());

        values.put(
                "lastUsedAt",
                session.lastUsedAt().toString()
        );
        values.put(
                "expiresAt",
                session.expiresAt().toString()
        );

        if (session.userAgent() != null) {
            values.put("userAgent", session.userAgent());
        }

        if (session.ipAddress() != null) {
            values.put("ipAddress", session.ipAddress());
        }

        return values;
    }

    public static AuthSession toDomain(
            SessionId sessionId,
            Map<Object, Object> values
    ) {
        return AuthSession.restore(
                sessionId,
                new UserId(
                        (String) values.get("userId")
                ),
                (String) values.get("refreshTokenHash"),
                Instant.parse(
                        (String) values.get("createdAt")
                ),
                Instant.parse(
                        (String) values.get("lastUsedAt")
                ),
                Instant.parse(
                        (String) values.get("expiresAt")
                ),
                (String) values.get("userAgent"),
                (String) values.get("ipAddress")
        );
    }
}
