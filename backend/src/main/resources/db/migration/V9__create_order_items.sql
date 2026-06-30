-- Purpose: Line items, snapshotted at order time (BR-18). Column set mirrors
-- order.model.OrderItem exactly (HIBERNATE_DDL_AUTO=validate).
--
-- BR-18 / §6.3: `item_name_snapshot` / `item_price_snapshot` are the values actually
-- displayed and billed; `menu_item_id` is kept only for analytics/traceability and is
-- never joined at query time for price display, so later menu price edits never alter
-- historical orders.
--
-- Cascading: order_items.order_id -> CASCADE. This isn't one of the explicit overrides
-- listed in SRS §6.4, but order.model.Order maps `orderItems` with
-- cascade=ALL+orphanRemoval=true — i.e. OrderItem is a true aggregate child of Order,
-- not an independently-owned row, so the DB-level behavior should mirror that JPA
-- ownership. order_items.menu_item_id stays RESTRICT per §6.4 (menu items are
-- soft-deleted only, BR-10, so this FK should never actually fire in practice).

CREATE TABLE order_items (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id               UUID NOT NULL,
    menu_item_id           UUID NOT NULL,
    quantity               INTEGER NOT NULL,
    item_name_snapshot     VARCHAR(500) NOT NULL,
    item_price_snapshot    BIGINT NOT NULL,
    subtotal               BIGINT NOT NULL,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE RESTRICT,
    CONSTRAINT ck_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_items_price_snapshot CHECK (item_price_snapshot >= 0),
    CONSTRAINT ck_order_items_subtotal CHECK (subtotal >= 0)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_menu_item_id ON order_items (menu_item_id);
