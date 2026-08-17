CREATE TABLE automation_runs(
    id                  VARCHAR(64) PRIMARY KEY,
    type                VARCHAR(100) NOT NULL,
    status              VARCHAR(32) NOT NULL,

    input_json          JSONB,
    output_json         JSONB,
    error_message       TEXT,

    requested_by_id     VARCHAR(64),
    approved_by_id      VARCHAR(64),

    duration_ms         BIGINT,

    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_automation_runs_requested_by
        FOREIGN KEY (requested_by_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_automation_runs_approved_by
        FOREIGN KEY (approved_by_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_automation_runs_status
        CHECK(
            status IN(
                'PENDING',
                'RUNNING',
                'SUCCESS',
                'FAILED',
                'NEEDS_APPROVAL',
                'APPROVED',
                'REJECTED'
            )
        )
);

CREATE INDEX idx_automation_runs_status
    ON automation_runs(status);

CREATE INDEX idx_automation_runs_type
    ON automation_runs(type);
