package com.example.axspring.automation.domain;

import java.time.Instant;
import java.util.Map;

import com.example.axspring.user.domain.UserId;

public class AutomationRun {
    private final String id;
    private final String type;
    
    private AutomationStatus status;

    private final Map<String, Object> input;
    private Map<String, Object> output;

    private String errorMessage;

    private final UserId requestedById;
    
    private Long durationMs;

    private final Instant createdAt;
    private Instant updatedAt;

    private AutomationRun(
        String id,
        String type,
        AutomationStatus status,
        Map<String, Object> input,
        Map<String, Object> output,
        String errorMessage,
        UserId requestedById,
        Long durationMs,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.input = input;
        this.output = output;
        this.errorMessage = errorMessage;
        this.requestedById = requestedById;
        this.durationMs = durationMs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt; 
    }

    public static AutomationRun start(
            String id,
            String type,
            Map<String, Object> input,
            UserId requestedById,
            Instant now
    ) {
        return new AutomationRun(
                id,
                type,
                AutomationStatus.RUNNING,
                input,
                null,
                null,
                requestedById,
                null,
                now,
                now
        );
    }

    public static AutomationRun restore(
            String id,
            String type,
            AutomationStatus status,
            Map<String, Object> input,
            Map<String, Object> output,
            String errorMessage,
            UserId requestedById,
            Long durationMs,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new AutomationRun(
                id,
                type,
                status,
                input,
                output,
                errorMessage,
                requestedById,
                durationMs,
                createdAt,
                updatedAt
        );
    }

    public void succeed(
        Map<String, Object> output,
        long durationMs,
        Instant now
    ) {
        this.status = AutomationStatus.SUCCESS;
        this.output = output;
        this.errorMessage = null;
        this.durationMs = durationMs;
        this.updatedAt = now;
    }

    public void fail(
        String errorMessage,
        long durationMs,
        Instant now
    ) {
        this.status = AutomationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
        this.updatedAt = now;
    }

     public String id() {
        return id;
    }

    public String type() {
        return type;
    }

    public AutomationStatus status() {
        return status;
    }

    public Map<String, Object> input() {
        return input;
    }

    public Map<String, Object> output() {
        return output;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public UserId requestedById() {
        return requestedById;
    }

    public Long durationMs() {
        return durationMs;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
