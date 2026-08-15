package com.example.axspring.user.domain;

public record Email(String value) {
    
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        value = value.trim().toLowerCase();
    }
}
