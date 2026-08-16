package com.example.axspring.ocr.adapter.in.web;

import com.example.axspring.ocr.domain.BusinessCardOcrResult;

public record BusinessCardOcrResponse(
    String name,
    String company,
    String department,
    String position,
    String email,
    String phone,
    double confidence
) {
    public static BusinessCardOcrResponse from(
        BusinessCardOcrResult result
    ) {
        return new BusinessCardOcrResponse(
                result.name(),
                result.company(),
                result.department(),
                result.position(),
                result.email(),
                result.phone(),
                result.confidence()
        );        
    } 
}
