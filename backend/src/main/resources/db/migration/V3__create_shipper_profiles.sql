-- Purpose: Shipper-specific data (vehicle, approval status) kept out of `users` since
-- it only applies to role=SHIPPER accounts (BR-32). Strict 1:1 with users via a unique
-- FK. No JPA entity exists yet, so this follows the SRS domain model (§6.1) directly.
--
-- BR-17/BR-32: a shipper cannot accept delivery jobs until status = APPROVED, even
-- though users.role is already SHIPPER from registration — that gate lives here, not
-- on the users table.

CREATE TABLE shipper_profiles (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID NOT NULL,
    vehicle_type      VARCHAR(50) NOT NULL,
    license_plate     VARCHAR(20) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    rejection_reason  VARCHAR(500),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_shipper_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT uk_shipper_profiles_user UNIQUE (user_id),
    CONSTRAINT uk_shipper_profiles_license_plate UNIQUE (license_plate),
    CONSTRAINT ck_shipper_profiles_status CHECK (status IN ('PENDING_APPROVAL', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_shipper_profiles_status ON shipper_profiles (status);
