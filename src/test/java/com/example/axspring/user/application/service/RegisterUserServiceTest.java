package com.example.axspring.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.axspring.user.application.port.in.RegisterUserCommand;
import com.example.axspring.user.application.port.out.PasswordEncoder;
import com.example.axspring.user.application.port.out.UserRepository;
import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerUserService = new RegisterUserService(
            userRepository,
            passwordEncoder
        );
    }

    @Test
    void enroll_user() {
        RegisterUserCommand command = new RegisterUserCommand(
                "홍길동",
                "user@example.com",
                "password1234"
        );

        when(userRepository.existsByEmail(any(Email.class)))
                .thenReturn(false);

        when(passwordEncoder.encode("password1234"))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = registerUserService.register(command);

        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.email().value())
                .isEqualTo("user@example.com");

        assertThat(result.passwordHash())
                .isEqualTo("hashed-password");

        verify(passwordEncoder)
                .encode("password1234");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void cannot_enroll_with_duplicate_email() {
        RegisterUserCommand command = new RegisterUserCommand(
            "홍길동",
            "user@example.com",
            "password1234"
        );

        when(userRepository.existsByEmail(any(Email.class)))
            .thenReturn(true);

        assertThatThrownBy(
                () -> registerUserService.register(command)
        ).isInstanceOf(IllegalStateException.class);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
