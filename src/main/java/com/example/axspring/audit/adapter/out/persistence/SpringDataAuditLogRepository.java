package com.example.axspring.audit.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAuditLogRepository
        extends JpaRepository<AuditLogJpaEntity, String> {
}
