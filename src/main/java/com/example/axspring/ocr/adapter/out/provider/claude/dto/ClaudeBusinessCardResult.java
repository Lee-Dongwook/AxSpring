package com.example.axspring.ocr.adapter.out.provider.claude.dto;

public record ClaudeBusinessCardResult(
        String name,
        String company,
        String department,
        String position,
        String email,
        String phone,
        double confidence
) {
}
