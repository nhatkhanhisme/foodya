-- Purpose: Saved delivery addresses (UC-C06). No JPA entity exists for this yet, so
-- this migration follows the SRS domain model (§6.1) directly rather than mirroring
-- existing code.
--
-- BR-22: latitude/longitude are bounds-checked at the DB level.
-- UC-C06 alt flow / §7.9 note: "set-default" must be atomic and exactly one address per
-- user may be default at a time — enforced here with a partial unique index rather than
-- application-level "unset all, then set one" logic, which is the only way to guarantee
-- the invariant under concurrent requests.

CREATE TABLE addresses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    label           VARCHAR(50) NOT NULL,
    recipient_name  VARCHAR(255) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    street          VARCHAR(255) NOT NULL,
    ward            VARCHAR(100),
    district        VARCHAR(100),
    city            VARCHAR(100) NOT NULL,
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT ck_addresses_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_addresses_longitude CHECK (longitude BETWEEN -180 AND 180)
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);

-- Guarantees "exactly one default address per user" at the database level (BR §7.9).
CREATE UNIQUE INDEX uk_addresses_one_default_per_user ON addresses (user_id) WHERE is_default = TRUE;
