package com.example.axspring.auth.application.exception;

public class InactiveUserException extends RuntimeException {

    public InactiveUserException() {
        super("Inactive user");
    }
}
