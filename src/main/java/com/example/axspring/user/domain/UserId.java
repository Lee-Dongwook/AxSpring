package com.example.axspring.user.domain;

public record UserId(String value) {
    
    public UserId {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId must not be blank");
        }
    }
}
