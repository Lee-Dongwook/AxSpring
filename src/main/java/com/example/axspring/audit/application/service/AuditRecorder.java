package com.example.axspring.audit.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.axspring.audit.application.port.out.AuditLogRepository;
import com.example.axspring.audit.domain.AuditLog;
import com.example.axspring.user.domain.UserId;

@Service
public class AuditRecorder {

    private final AuditLogRepository auditLogRepository;

    public AuditRecorder(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            UserId actorId,
            String action,
            String entityType,
            String entityId,
            Map<String, Object> before,
            Map<String, Object> after,
            String ipAddress,
            String userAgent
    ) {
        AuditLog auditLog = AuditLog.create(
                UUID.randomUUID().toString(),
                actorId,
                action,
                entityType,
                entityId,
                before,
                after,
                ipAddress,
                userAgent,
                Instant.now()
        );

        auditLogRepository.save(auditLog);
    }
}
