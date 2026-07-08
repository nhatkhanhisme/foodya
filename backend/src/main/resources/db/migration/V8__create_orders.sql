-- Purpose: Order aggregate root and its state machine (§8). Column set mirrors
-- order.model.Order exactly (HIBERNATE_DDL_AUTO=validate): `total`, `shipping_fee`,
-- `subtotal`, `delivery_address_snapshot` and `delivery_address_id` are the
-- already-renamed/added columns the entity's code comments describe (no separate
-- "rename" migrations are needed since this is the first time these tables exist).
--
-- Enum strategy: `status` and `distance_source` are VARCHAR + CHECK, consistent with
-- earlier migrations. The full PENDING/AWAITING_PAYMENT/.../CANCELLED set and allowed
-- transitions (§8.2) are enforced in the service layer (Order.updateStatus); the CHECK
-- constraint here only guards against an invalid status value ever being written,
-- not against illegal *transitions* (DB constraints can't express a state machine).
--
-- BR-13: `version` backs Hibernate's @Version optimistic lock for concurrent shipper
-- assignment (UC-S03).
-- delivery_address_id is nullable to match the entity's current mapping (kept nullable
-- "for pre-migration rows" per its code comment); on a fresh database this column should
-- always be populated by the application going forward.

CREATE TABLE orders (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id                 UUID NOT NULL,
    restaurant_id               UUID NOT NULL,
    shipper_id                  UUID,
    delivery_address_snapshot   VARCHAR(500),
    delivery_address_id         UUID,
    total                       BIGINT NOT NULL,
    shipping_fee                BIGINT NOT NULL DEFAULT 0,
    subtotal                    BIGINT NOT NULL DEFAULT 0,
    total_items                 INTEGER NOT NULL,
    status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    order_date                  TIMESTAMPTZ NOT NULL,
    cancel_reason                VARCHAR(500),
    order_notes                 VARCHAR(1000),
    distance_km                 NUMERIC(10, 2),
    distance_source              VARCHAR(10),
    version                     INTEGER NOT NULL DEFAULT 0,
    confirmed_at                TIMESTAMPTZ,
    picked_up_at                TIMESTAMPTZ,
    delivered_at                TIMESTAMPTZ,
    cancelled_at                TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_shipper FOREIGN KEY (shipper_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_delivery_address FOREIGN KEY (delivery_address_id) REFERENCES addresses (id) ON DELETE RESTRICT,
    CONSTRAINT ck_orders_status CHECK (status IN
        ('PENDING', 'AWAITING_PAYMENT', 'CONFIRMED', 'REJECTED', 'READY_FOR_PICKUP', 'PICKED_UP', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT ck_orders_distance_source CHECK (distance_source IS NULL OR distance_source IN ('PROVIDER', 'FALLBACK')),
    CONSTRAINT ck_orders_total CHECK (total >= 0),
    CONSTRAINT ck_orders_shipping_fee CHECK (shipping_fee >= 0),
    CONSTRAINT ck_orders_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_orders_total_items CHECK (total_items >= 0)
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_restaurant_id ON orders (restaurant_id);
CREATE INDEX idx_orders_shipper_id ON orders (shipper_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_delivery_address_id ON orders (delivery_address_id);
