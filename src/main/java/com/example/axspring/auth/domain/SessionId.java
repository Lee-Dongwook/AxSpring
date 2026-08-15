package com.example.axspring.auth.domain;

public record SessionId(String value) {
    
    public SessionId {
        if (value == null || value.isBlank()) {
             throw new IllegalArgumentException(
                    "SessionId must not be blank"
            );
        }
    }
}
