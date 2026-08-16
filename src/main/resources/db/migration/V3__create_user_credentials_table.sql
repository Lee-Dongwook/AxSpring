CREATE TABLE user_credentials (
    user_id                 VARCHAR(64)  PRIMARY KEY,
    password_hash           VARCHAR(255) NOT NULL,
    must_change_password    BOOLEAN      NOT NULL DEFAULT TRUE,
    password_changed_at     TIMESTAMPTZ,

    CONSTRAINT fk_user_credentials_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
