package com.example.axspring.auth.application.port.in;

import com.example.axspring.user.domain.User;

public record LoginResult(
        User user,
        String accessToken,
        String refreshToken
) {
}
