package com.example.axspring.global.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.example.axspring.ocr.adapter.out.provider.claude.ClaudeOcrProperties;

@Configuration
@ConditionalOnProperty(name = "app.ocr.provider", havingValue = "claude")
@EnableConfigurationProperties(ClaudeOcrProperties.class)
public class ClaudeConfig {
    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.fromEnv();
    }
}
