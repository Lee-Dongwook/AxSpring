CREATE TABLE users (
    id              VARCHAR(64)  PRIMARY KEY,

    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(320) NOT NULL,
    image_url       TEXT,

    role            VARCHAR(32)  NOT NULL DEFAULT 'MEMBER',

    department      VARCHAR(100),
    position        VARCHAR(100),
    hire_date       DATE,
    birth_date      DATE,

    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

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

CREATE TABLE user_credentials (
    user_id                 VARCHAR(64) PRIMARY KEY,

    password_hash           VARCHAR(255) NOT NULL,
    must_change_password    BOOLEAN      NOT NULL DEFAULT TRUE,
    password_changed_at     TIMESTAMPTZ,

    CONSTRAINT fk_user_credentials_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
