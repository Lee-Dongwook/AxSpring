package com.example.axspring.automation.adapter.out.persistence;

import java.time.Instant;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.axspring.automation.domain.AutomationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "automation_runs")
public class AutomationRunJpaEntity {
    
    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "type", length = 100, nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private AutomationStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_json", columnDefinition = "jsonb")
    private Map<String, Object> input;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "output_json", columnDefinition = "jsonb")
    private Map<String, Object> output;
    
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "requested_by_id", length = 64)
    private String requestedById;

    @Column(name = "approved_by_id", length = 64)
    private String approvedById;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AutomationRunJpaEntity() {

    }

    public AutomationRunJpaEntity(
            String id,
            String type,
            AutomationStatus status,
            Map<String, Object> input,
            Map<String, Object> output,
            String errorMessage,
            String requestedById,
            String approvedById,
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
        this.approvedById = approvedById;
        this.durationMs = durationMs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public AutomationStatus getStatus() {
        return status;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRequestedById() {
        return requestedById;
    }

    public String getApprovedById() {
        return approvedById;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
