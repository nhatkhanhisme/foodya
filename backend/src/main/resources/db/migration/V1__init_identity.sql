-- identity module: com.foodya.foodya_backend.identity.domain.User

CREATE TABLE users (
    id                        UUID PRIMARY KEY,
    username                  VARCHAR(50)  NOT NULL,
    email                     VARCHAR(255) NOT NULL,
    password_hash             VARCHAR(255) NOT NULL,
    full_name                 VARCHAR(255) NOT NULL,
    phone                     VARCHAR(20),
    role                      VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER',
    profile_image_url         VARCHAR(500),
    status                    VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    is_email_verified         BOOLEAN      NOT NULL DEFAULT FALSE,
    is_phone_number_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at             TIMESTAMPTZ,
    created_at                TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_phone UNIQUE (phone)
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
