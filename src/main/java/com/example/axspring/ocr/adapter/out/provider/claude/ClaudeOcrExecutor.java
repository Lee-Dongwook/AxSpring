package com.example.axspring.ocr.adapter.out.provider.claude;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.example.axspring.ocr.application.exception.OcrProviderException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

@Component
public class ClaudeOcrExecutor {

    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public ClaudeOcrExecutor(
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.retry =
                retryRegistry.retry("claudeOcr");

        this.circuitBreaker =
                circuitBreakerRegistry
                        .circuitBreaker("claudeOcr");
    }

    public <T> T execute(
            Supplier<T> operation
    ) {
        try {
            Supplier<T> withCircuitBreaker =
                    CircuitBreaker.decorateSupplier(
                            circuitBreaker,
                            operation
                    );

            Supplier<T> withRetry =
                    Retry.decorateSupplier(
                            retry,
                            withCircuitBreaker
                    );

            return withRetry.get();

        } catch (Exception e) {
            throw new OcrProviderException(
                    "OCR provider unavailable",
                    e
            );
        }
    }
}
