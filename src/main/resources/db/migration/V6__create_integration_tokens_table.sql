CREATE TABLE integration_tokens (
    id                  VARCHAR(64) PRIMARY KEY,

    user_id             VARCHAR(64)  NOT NULL,
    provider            VARCHAR(32)  NOT NULL,

    access_token_enc    TEXT         NOT NULL,
    refresh_token_enc   TEXT,

    expires_at          TIMESTAMPTZ  NOT NULL,

    scope               TEXT         NOT NULL,
    token_type          VARCHAR(32)  NOT NULL DEFAULT 'Bearer',

    google_email        VARCHAR(320),

    revoked_at          TIMESTAMPTZ,

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_integration_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_integration_tokens_provider
        CHECK (
            provider IN (
                'GOOGLE'
            )
        ),

    CONSTRAINT uk_integration_tokens_user_provider
        UNIQUE (user_id, provider)
);

CREATE INDEX idx_integration_tokens_provider
    ON integration_tokens(provider);
