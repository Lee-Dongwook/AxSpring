package com.example.axspring.auth.adapter.in.web;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.axspring.auth.application.port.in.LoginCommand;
import com.example.axspring.auth.application.port.in.LoginResult;
import com.example.axspring.auth.application.port.in.LoginUseCase;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {        
        LoginCommand command = new LoginCommand(
            request.email(),
            request.password()
        );

        LoginResult result = loginUseCase.login(command);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(false) // local only
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(30))
                .build();
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(LoginResponse.from(result));
    }
    
}
