-- Purpose: Foundational identity table for all four actor roles (Customer, Restaurant
-- Owner, Shipper, Admin — BR-01). Every other module hangs off this table, so it is
-- the first migration. Column lengths/nullability mirror user.model.User exactly
-- (HIBERNATE_DDL_AUTO=validate requires this) — see AGENTS note in PR description.
--
-- Enum strategy: `role` is stored as VARCHAR + CHECK constraint, not a native Postgres
-- ENUM type. Hibernate maps @Enumerated(EnumType.STRING) to a plain string column, and
-- Postgres ENUM types can't have values added inside a transaction in older PG versions
-- and require a dedicated ALTER TYPE migration for every change — a CHECK constraint is
-- simpler to evolve and matches what's already coded.
--
-- BR-30: `status` (ACTIVE|BANNED) is the SRS-specified field — the entity previously
-- modeled this as separate `is_active` + `account_locked` booleans; both are now
-- collapsed into this single column, matching BR-30's "no other status values exist."

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username                    VARCHAR(50)  NOT NULL,
    email                       VARCHAR(255) NOT NULL,
    password_hash               VARCHAR(255) NOT NULL,
    full_name                   VARCHAR(255) NOT NULL,
    phone                       VARCHAR(255),
    role                        VARCHAR(255) NOT NULL DEFAULT 'CUSTOMER',
    profile_image_url           VARCHAR(255),
    status                      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_email_verified           BOOLEAN NOT NULL DEFAULT FALSE,
    is_phone_number_verified    BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at               TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone UNIQUE (phone),
    CONSTRAINT ck_users_role CHECK (role IN ('CUSTOMER', 'RESTAURANT_OWNER', 'SHIPPER', 'ADMIN')),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'BANNED'))
);

CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_status ON users (status);
