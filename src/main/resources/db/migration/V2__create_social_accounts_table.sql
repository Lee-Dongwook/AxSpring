CREATE TABLE social_accounts(
    id                  BIGSERIAL    PRIMARY KEY,
    user_id             VARCHAR(64)  NOT NULL,
    provider            VARCHAR(32)  NOT NULL,
    provider_user_id    VARCHAR(255) NOT NULL,
    email               VARCHAR(320),

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_social_accounts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    
    CONSTRAINT chk_social_accounts_provider
        CHECK (
            provider IN (
                'GOOGLE'
            )
        ),
    
    CONSTRAINT uk_social_accounts_provider_user
        UNIQUE (provider, provider_user_id),

    CONSTRAINT uk_social_accounts_user_provider
        UNIQUE (user_id, provider)
)
