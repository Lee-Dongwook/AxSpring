package com.example.axspring.audit.domain;

import java.time.Instant;
import java.util.Map;

import com.example.axspring.user.domain.UserId;

public class AuditLog {
    private final String id;
    private final UserId actorId;

    private final String action;
    private final String entityType;
    private final String entityId;

    private final Map<String, Object> before;
    private final Map<String, Object> after;

    private final String ipAddress;
    private final String userAgent;

    private final Instant createdAt;

    private AuditLog(
            String id,
            UserId actorId,
            String action,
            String entityType,
            String entityId,
            Map<String, Object> before,
            Map<String, Object> after,
            String ipAddress,
            String userAgent,
            Instant createdAt
    ) {
        this.id = id;
        this.actorId = actorId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.before = before;
        this.after = after;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    public static AuditLog create(
            String id,
            UserId actorId,
            String action,
            String entityType,
            String entityId,
            Map<String, Object> before,
            Map<String, Object> after,
            String ipAddress,
            String userAgent,
            Instant now
    ) {
        return new AuditLog(
                id,
                actorId,
                action,
                entityType,
                entityId,
                before,
                after,
                ipAddress,
                userAgent,
                now);
    }
    
    public String id() {
        return id;
    }

    public UserId actorId() {
        return actorId;
    }

    public String action() {
        return action;
    }

    public String entityType() {
        return entityType;
    }

    public String entityId() {
        return entityId;
    }

    public Map<String, Object> before() {
        return before;
    }

    public Map<String, Object> after() {
        return after;
    }

    public String ipAddress() {
        return ipAddress;
    }

    public String userAgent() {
        return userAgent;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
