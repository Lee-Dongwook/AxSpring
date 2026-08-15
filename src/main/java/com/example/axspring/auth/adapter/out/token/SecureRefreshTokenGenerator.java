package com.example.axspring.auth.adapter.out.token;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.example.axspring.auth.application.port.out.RefreshTokenGenerator;

@Component
public class SecureRefreshTokenGenerator implements RefreshTokenGenerator {
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }
}
