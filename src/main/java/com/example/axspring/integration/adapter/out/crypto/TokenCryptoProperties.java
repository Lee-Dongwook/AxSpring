package com.example.axspring.integration.adapter.out.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.integration.crypto")
public record TokenCryptoProperties(
    String key
) {
    
}
