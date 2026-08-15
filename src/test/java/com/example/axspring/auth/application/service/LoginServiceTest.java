package com.example.axspring.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.axspring.auth.adapter.out.token.JwtProperties;
import com.example.axspring.auth.application.port.in.LoginCommand;
import com.example.axspring.auth.application.port.in.LoginResult;
import com.example.axspring.auth.application.port.out.AuthSessionRepository;
import com.example.axspring.auth.application.port.out.RefreshTokenGenerator;
import com.example.axspring.auth.application.port.out.TokenIssuer;
import com.example.axspring.auth.application.port.out.UserCredentialRepository;
import com.example.axspring.auth.domain.AuthSession;
import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.application.port.out.PasswordEncoder;
import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserCredentialRepository credentialRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthSessionRepository authSessionRepository;

    @Mock
    RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    TokenIssuer tokenIssuer;

    LoginService loginService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(
                "axspring-auth",
                "axspring-api",
                "local-key-1",
                "private.pem",
                "public.pem",
                Duration.ofMinutes(15),
                Duration.ofDays(14)
        );
        loginService = new LoginService(
                userRepository,
                credentialRepository,
                passwordEncoder,
                authSessionRepository,
                refreshTokenGenerator,
                tokenIssuer,
                jwtProperties
        );
    }

    @Test
    void login_creates_session_and_returns_tokens() {
        UserId userId = new UserId("user-1");
        User user = User.register(
                userId,
                "홍길동",
                new Email("user@example.com"),
                Instant.now()
        );
        UserCredential credential = UserCredential.create(userId, "password-hash");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(java.util.Optional.of(user));
        when(credentialRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(credential));
        when(passwordEncoder.matches("password1234", "password-hash")).thenReturn(true);
        when(refreshTokenGenerator.generate()).thenReturn("refresh-token");
        when(passwordEncoder.encode("refresh-token")).thenReturn("refresh-token-hash");
        when(tokenIssuer.issueAccessToken(eq(userId), any(), eq(user.role())))
                .thenReturn("access-token");

        LoginResult result = loginService.login(
                new LoginCommand("user@example.com", "password1234")
        );

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<AuthSession> sessionCaptor = ArgumentCaptor.forClass(AuthSession.class);
        verify(authSessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().userId()).isEqualTo(userId);
        assertThat(sessionCaptor.getValue().refreshTokenHash()).isEqualTo("refresh-token-hash");
        verify(tokenIssuer).issueAccessToken(eq(userId), eq(sessionCaptor.getValue().id()), eq(user.role()));
    }
}
