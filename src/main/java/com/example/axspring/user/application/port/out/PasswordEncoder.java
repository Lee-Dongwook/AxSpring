package com.example.axspring.user.application.port.out;

public interface PasswordEncoder {

    String encode(String rawPassword);
}
