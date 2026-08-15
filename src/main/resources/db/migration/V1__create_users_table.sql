CREATE TABLE users (
    id                      VARCHAR(64)     PRIMARY KEY,

    name                    VARCHAR(100)    NOT NULL,
    email                   VARCHAR(320)    NOT NULL,
    email_verified_at       TIMESTAMPTZ,
    image_url               TEXT,

    password_hash           VARCHAR(255),

    role                    VARCHAR(32)     NOT NULL DEFAULT 'MEMBER',

    department              VARCHAR(100),
    position                VARCHAR(100),
    hire_date               DATE,
    birth_date              DATE,

    slack_user_id           VARCHAR(100),
    google_account_id       VARCHAR(255),
    notion_person_id        VARCHAR(255),
    linear_user_id          VARCHAR(255),
    github_login            VARCHAR(255),

    email_aliases           JSONB           NOT NULL DEFAULT '[]'::jsonb,

    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    must_change_password    BOOLEAN         NOT NULL DEFAULT TRUE,
    password_changed_at     TIMESTAMPTZ,

    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_users_role CHECK (
        role IN (
            'OWNER',
            'ADMIN',
            'MANAGER',
            'MEMBER',
            'VIEWER'
        )
    )
);

CREATE UNIQUE INDEX uk_users_email_lower
    ON users (LOWER(email));
