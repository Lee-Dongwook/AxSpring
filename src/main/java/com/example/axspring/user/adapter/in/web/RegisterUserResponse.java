package com.example.axspring.user.adapter.in.web;

import java.time.Instant;

import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserRole;

public record RegisterUserResponse(
    String id,
    String name,
    String email,
    UserRole role,
    boolean active,
    Instant createdAt
) {
    public static RegisterUserResponse from(User user){
        return new RegisterUserResponse(
                user.id().value(),
                user.name(),
                user.email().value(),
                user.role(),
                user.active(),
                user.createdAt()
        );
    }
}
