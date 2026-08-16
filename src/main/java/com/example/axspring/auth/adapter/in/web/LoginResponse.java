package com.example.axspring.auth.adapter.in.web;

import com.example.axspring.auth.application.port.in.LoginResult;
import com.example.axspring.user.domain.UserRole;

public record LoginResponse(
    String accessToken,
    String tokenType,
    String userId,
    String name,
    String email,
    UserRole role
) {
    
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
            result.accessToken(), 
            "Bearer",
            result.user().id().value(),
            result.user().name(), 
            result.user().email().value(),
            result.user().role()
        );
    }   
}
