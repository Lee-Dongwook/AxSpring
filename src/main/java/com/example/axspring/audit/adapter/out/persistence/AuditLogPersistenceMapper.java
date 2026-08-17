package com.example.axspring.audit.adapter.out.persistence;

import com.example.axspring.audit.domain.AuditLog;
import com.example.axspring.user.domain.UserId;

public final class AuditLogPersistenceMapper {

    private AuditLogPersistenceMapper() {
    }

    public static AuditLogJpaEntity toEntity(AuditLog auditLog) {
        return new AuditLogJpaEntity(
                auditLog.id(),
                auditLog.actorId() == null
                        ? null
                        : auditLog.actorId().value(),
                auditLog.action(),
                auditLog.entityType(),
                auditLog.entityId(),
                auditLog.before(),
                auditLog.after(),
                auditLog.ipAddress(),
                auditLog.userAgent(),
                auditLog.createdAt()
        );
    }

    public static AuditLog toDomain(AuditLogJpaEntity entity) {
        return AuditLog.restore(
                entity.getId(),
                entity.getActorId() == null
                        ? null
                        : new UserId(entity.getActorId()),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getBefore(),
                entity.getAfter(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getCreatedAt()
        );
    }
}
