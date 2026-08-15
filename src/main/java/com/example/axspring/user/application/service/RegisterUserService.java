package com.example.axspring.user.application.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.axspring.user.application.port.in.RegisterUserCommand;
import com.example.axspring.user.application.port.in.RegisterUserUseCase;
import com.example.axspring.user.application.port.out.PasswordEncoder;
import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserId;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public RegisterUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterUserCommand command) {
        Email email = new Email(command.email());

        if(userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already exists");
        }

        String passwordHash =
                passwordEncoder.encode(command.password());

        Instant now  = Instant.now();

        User user = User.register(
            new UserId(generateUserId()),
            command.name(),
            email,
            passwordHash,
            now
        );

        return userRepository.save(user);
    }

    private String generateUserId() {
        return  java.util.UUID.randomUUID().toString();
    }
}
