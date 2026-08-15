package com.example.axspring.user.application.port.in;

public record RegisterUserCommand(
        String name,
        String email,
        String password
) {
}
