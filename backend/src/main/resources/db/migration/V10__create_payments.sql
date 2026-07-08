-- Purpose: Payment record for COD and online (VNPay/Momo) orders (BR-28). No JPA entity
-- exists yet, so this follows the SRS domain model (§6.1, §6.4) directly.
--
-- BR-28: every Order has exactly one Payment row regardless of method —
-- uk_payments_order enforces the 1:1 at the DB level.
-- BR-26: provider webhooks are processed idempotently keyed by provider_txn_ref —
-- uk_payments_provider_txn_ref makes a duplicate delivery a constraint violation at the
-- DB layer too (defense in depth alongside the service-layer idempotency check); NULL is
-- allowed (and not counted as a duplicate) for COD rows, which have no provider ref.
-- §6.4: provider must be NULL for COD and one of VNPAY/MOMO for ONLINE —
-- ck_payments_provider_consistency enforces that pairing directly.
-- §6.4: Payment.order_id -> Order is RESTRICT — a payment is a financial record and
-- must never disappear as a side effect of an order operation.

CREATE TABLE payments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL,
    method              VARCHAR(10) NOT NULL,
    provider            VARCHAR(10),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider_txn_ref     VARCHAR(100),
    amount              BIGINT NOT NULL,
    paid_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT,
    CONSTRAINT uk_payments_order UNIQUE (order_id),
    CONSTRAINT uk_payments_provider_txn_ref UNIQUE (provider_txn_ref),
    CONSTRAINT ck_payments_method CHECK (method IN ('COD', 'ONLINE')),
    CONSTRAINT ck_payments_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    CONSTRAINT ck_payments_amount CHECK (amount >= 0),
    CONSTRAINT ck_payments_provider_consistency CHECK (
        (method = 'COD' AND provider IS NULL) OR
        (method = 'ONLINE' AND provider IN ('VNPAY', 'MOMO'))
    )
);

CREATE INDEX idx_payments_status ON payments (status);
