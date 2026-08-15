package com.example.axspring.auth.application.port.in;

import com.example.axspring.user.domain.User;

public interface LoginUseCase {

    User login(LoginCommand command);
}
