package com.example.axspring.audit.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.example.axspring.audit.application.port.out.AuditLogRepository;
import com.example.axspring.audit.domain.AuditLog;

@Repository
public class JpaAuditLogRepositoryAdapter
        implements AuditLogRepository {

    private final SpringDataAuditLogRepository repository;

    public JpaAuditLogRepositoryAdapter(
            SpringDataAuditLogRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogJpaEntity saved =
                repository.save(
                        AuditLogPersistenceMapper.toEntity(auditLog)
                );

        return AuditLogPersistenceMapper.toDomain(saved);
    }
}
