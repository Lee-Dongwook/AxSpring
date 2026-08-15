package com.example.axspring.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.axspring.auth.application.exception.InactiveUserException;
import com.example.axspring.auth.application.exception.InvalidCredentialsException;
import com.example.axspring.auth.application.port.in.LoginCommand;
import com.example.axspring.auth.application.port.in.LoginUseCase;
import com.example.axspring.auth.application.port.out.UserCredentialRepository;
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

    public LoginService(
            UserRepository userRepository,
            UserCredentialRepository credentialRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User login(LoginCommand command) {
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

        return user;
    }
}
