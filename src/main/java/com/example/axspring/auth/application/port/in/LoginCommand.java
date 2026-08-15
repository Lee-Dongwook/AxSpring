package com.example.axspring.auth.application.port.in;

public record LoginCommand(
        String email,
        String password
) {
}
