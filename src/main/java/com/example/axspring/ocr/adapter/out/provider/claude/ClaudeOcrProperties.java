package com.example.axspring.ocr.adapter.out.provider.claude;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ocr.claude")
public record ClaudeOcrProperties(
    String model,
    int maxTokens,
    Duration timeout
) {
    
}
