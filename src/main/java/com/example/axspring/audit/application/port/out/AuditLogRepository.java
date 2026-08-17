package com.example.axspring.audit.application.port.out;

import com.example.axspring.audit.domain.AuditLog;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);
}
