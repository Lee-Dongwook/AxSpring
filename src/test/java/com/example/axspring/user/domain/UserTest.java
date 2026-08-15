package com.example.axspring.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void user_enroll_default() {
        Instant now = Instant.parse("2026-08-15T04:00:00Z");

        User user = User.register(
                new UserId("user-1"),
                "홍길동",
                new Email("user@example.com"),
                "hashed-password",
                now
        );

        assertThat(user.id().value()).isEqualTo("user-1");
        assertThat(user.name()).isEqualTo("홍길동");
        assertThat(user.email().value()).isEqualTo("user@example.com");

        assertThat(user.role()).isEqualTo(UserRole.MEMBER);
        assertThat(user.active()).isTrue();
        assertThat(user.mustChangePassword()).isTrue();

        assertThat(user.createdAt()).isEqualTo(now);
        assertThat(user.updatedAt()).isEqualTo(now);
    }
}
