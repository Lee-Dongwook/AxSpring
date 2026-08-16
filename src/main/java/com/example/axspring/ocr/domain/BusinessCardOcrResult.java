package com.example.axspring.ocr.domain;

public record BusinessCardOcrResult(
    String name,
    String company,
    String department,
    String position,
    String email,
    String phone,
    double confidence
) {
    
}
