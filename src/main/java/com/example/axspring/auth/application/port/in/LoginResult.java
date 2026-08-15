package com.example.axspring.auth.application.port.in;

public record LoginResult(
        String accessToken,
        String refreshToken
) {
}
