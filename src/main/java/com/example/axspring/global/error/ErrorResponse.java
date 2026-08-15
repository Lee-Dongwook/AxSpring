package com.example.axspring.global.error;

public record ErrorResponse(
        String code,
        String message,
        String requestId
) {
}
