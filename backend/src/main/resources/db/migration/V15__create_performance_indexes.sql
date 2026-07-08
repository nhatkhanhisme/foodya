-- Purpose: Cross-cutting indexes for hot read paths that don't belong to a single
-- table's own creation migration. Unique constraints already created their own indexes
-- (uk_users_email, uk_payments_order, etc.) and are not repeated here.

-- UC-R04: restaurant's "incoming orders" view filters by restaurant_id + status.
CREATE INDEX idx_orders_restaurant_status ON orders (restaurant_id, status);

-- UC-S02: "available delivery jobs" lists unassigned READY_FOR_PICKUP orders; the
-- partial index keeps this small regardless of total order volume.
CREATE INDEX idx_orders_unassigned_ready ON orders (restaurant_id)
    WHERE status = 'READY_FOR_PICKUP' AND shipper_id IS NULL;

-- UC-C04: customer-facing menu view filters by restaurant_id + availability, excluding
-- soft-deleted items.
CREATE INDEX idx_menu_items_restaurant_available ON menu_items (restaurant_id, is_available)
    WHERE is_deleted = FALSE;
