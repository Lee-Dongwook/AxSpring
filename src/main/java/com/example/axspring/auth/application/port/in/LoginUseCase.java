package com.example.axspring.auth.application.port.in;

public interface LoginUseCase {

    LoginResult login(LoginCommand command);
}
