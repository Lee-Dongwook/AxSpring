package com.example.axspring.user.application.port.in;

import com.example.axspring.user.domain.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);
}
