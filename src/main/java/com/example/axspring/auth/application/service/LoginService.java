package com.example.axspring.auth.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.axspring.auth.application.exception.InactiveUserException;
import com.example.axspring.auth.application.exception.InvalidCredentialsException;
import com.example.axspring.auth.application.port.in.LoginCommand;
import com.example.axspring.auth.application.port.in.LoginResult;
import com.example.axspring.auth.application.port.in.LoginUseCase;
import com.example.axspring.auth.application.port.out.AuthSessionRepository;
import com.example.axspring.auth.application.port.out.RefreshTokenGenerator;
import com.example.axspring.auth.application.port.out.TokenIssuer;
import com.example.axspring.auth.application.port.out.UserCredentialRepository;
import com.example.axspring.auth.adapter.out.token.JwtProperties;
import com.example.axspring.auth.domain.AuthSession;
import com.example.axspring.auth.domain.SessionId;
import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.application.port.out.PasswordEncoder;
import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

@Service
public class LoginService implements LoginUseCase {
    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSessionRepository authSessionRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final TokenIssuer tokenIssuer;
    private final JwtProperties jwtProperties;

    public LoginService(
            UserRepository userRepository,
            UserCredentialRepository credentialRepository,
            PasswordEncoder passwordEncoder,
            AuthSessionRepository authSessionRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            TokenIssuer tokenIssuer,
            JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSessionRepository = authSessionRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenIssuer = tokenIssuer;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public LoginResult login(LoginCommand command) {
        Email email = new Email(command.email());

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.active()) {
            throw new InactiveUserException();
        }

        UserCredential credential = credentialRepository
                .findByUserId(user.id())
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                command.password(),
                credential.passwordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        Instant now = Instant.now();
        SessionId sessionId = new SessionId(UUID.randomUUID().toString());
        String refreshToken = refreshTokenGenerator.generate();

        AuthSession session = AuthSession.create(
                sessionId,
                user.id(),
                passwordEncoder.encode(refreshToken),
                now,
                now.plus(jwtProperties.refreshTokenTtl()),
                null,
                null
        );
        authSessionRepository.save(session);

        String accessToken = tokenIssuer.issueAccessToken(
                user.id(),
                sessionId,
                user.role()
        );

        return new LoginResult(user, accessToken, refreshToken);
    }
}
