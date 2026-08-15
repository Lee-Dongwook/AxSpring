package com.example.axspring.user.application.exception;

public class DuplicateEmailException
        extends RuntimeException {

    public DuplicateEmailException() {
        super("Email already exists");
    }
}
