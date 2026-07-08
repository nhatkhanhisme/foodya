-- Purpose: Append-only trail of every Admin moderation action (BR-20: ban/unban,
-- restaurant/shipper approval/rejection, dispute resolution). No JPA entity exists yet,
-- so this follows the SRS domain model (§6.1, §6.3) directly.
--
-- §6.3: target is referenced generically via (target_type, target_id) rather than a
-- typed FK per target, since a single admin action can target a User, Restaurant, or
-- ShipperProfile row — a normal FK can't point at "one of several tables".
-- BR-20: "AuditLog rows are append-only — never updated or deleted." This is enforced
-- here with a trigger, not just left as an application-layer convention, since audit
-- trails are exactly the kind of record where a single missed safeguard (an accidental
-- UPDATE, a buggy cleanup job) defeats their entire purpose.

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id   UUID NOT NULL,
    action          VARCHAR(100) NOT NULL,
    target_type     VARCHAR(50) NOT NULL,
    target_id       UUID NOT NULL,
    reason          VARCHAR(1000),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_audit_logs_actor_user_id ON audit_logs (actor_user_id);
CREATE INDEX idx_audit_logs_target ON audit_logs (target_type, target_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at DESC);

CREATE OR REPLACE FUNCTION fn_audit_logs_prevent_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'audit_logs rows are append-only and cannot be updated or deleted (BR-20)';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_logs_prevent_update
    BEFORE UPDATE ON audit_logs
    FOR EACH ROW EXECUTE FUNCTION fn_audit_logs_prevent_mutation();

CREATE TRIGGER trg_audit_logs_prevent_delete
    BEFORE DELETE ON audit_logs
    FOR EACH ROW EXECUTE FUNCTION fn_audit_logs_prevent_mutation();
