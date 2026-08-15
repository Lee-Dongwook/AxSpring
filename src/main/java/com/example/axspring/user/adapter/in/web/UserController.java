package com.example.axspring.user.adapter.in.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.axspring.user.application.port.in.RegisterUserCommand;
import com.example.axspring.user.application.port.in.RegisterUserUseCase;
import com.example.axspring.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Users",
    description = "사용자 API"
)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;

    public UserController(
        RegisterUserUseCase registerUserUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @Operation(
        summary = "회원 가입",
        description = "새로운 사용자를 생성한다."
    )
    @PostMapping
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.email(),
                request.password()
        );

        User user = registerUserUseCase.register(command);

        RegisterUserResponse response =
                RegisterUserResponse.from(user);

        return ResponseEntity
                .created(URI.create("/api/users/" + user.id().value()))
                .body(response);
    }
}
