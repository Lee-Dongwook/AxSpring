package com.example.axspring.user.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.axspring.auth.application.port.out.UserCredentialRepository;
import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.application.port.in.RegisterUserCommand;
import com.example.axspring.user.application.port.in.RegisterUserUseCase;
import com.example.axspring.user.application.port.out.PasswordEncoder;
import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.application.exception.DuplicateEmailException;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserId;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    
    public RegisterUserService(
        UserRepository userRepository,
        UserCredentialRepository credentialRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterUserCommand command) {
        Email email = new Email(command.email());

        if(userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        UserId userId = new UserId(
                UUID.randomUUID().toString()
        );

        Instant now  = Instant.now();

        User user = User.register(
            userId,
            command.name(),
            email,
            now
        );

        String passwordHash =
                passwordEncoder.encode(command.password());

        UserCredential credential =
                UserCredential.create(
                        userId,
                        passwordHash
                );

        User savedUser = userRepository.save(user);
        credentialRepository.save(credential);
        return savedUser;
    }
}
