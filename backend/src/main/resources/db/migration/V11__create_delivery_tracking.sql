-- Purpose: Append-only GPS breadcrumb trail for an active delivery (UC-S04), broadcast
-- to the customer via SSE (§9). No JPA entity exists yet, so this follows the SRS
-- domain model (§6.1, §6.4) directly.
--
-- No created_at/updated_at: `recorded_at` already is the event timestamp for this
-- write-once log row, so a separate created_at would just duplicate it.
-- §6.4: DeliveryTracking.order_id -> Order is CASCADE — tracking history has no
-- independent meaning once its order is gone.
-- BR-22: latitude/longitude are bounds-checked at the DB level.

CREATE TABLE delivery_tracking (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL,
    shipper_id      UUID NOT NULL,
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_delivery_tracking_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_tracking_shipper FOREIGN KEY (shipper_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT ck_delivery_tracking_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_delivery_tracking_longitude CHECK (longitude BETWEEN -180 AND 180)
);

-- Supports "latest location for this order" and history-in-order queries (UC-C08/UC-S04).
CREATE INDEX idx_delivery_tracking_order_recorded_at ON delivery_tracking (order_id, recorded_at DESC);
