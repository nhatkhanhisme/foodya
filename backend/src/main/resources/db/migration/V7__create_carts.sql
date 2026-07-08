-- Purpose: Active checkout session (UC-C05). No JPA entity exists yet, so this follows
-- the SRS domain model (§6.1, §6.3) directly.
--
-- BR-05: a cart belongs to exactly one restaurant at a time, and §6.3 states a customer
-- has at most one open cart — uk_carts_customer enforces the latter at the DB level so
-- "add item from a different restaurant" is unambiguous: there is only ever one cart
-- row per customer to either reuse or replace.
--
-- Cascading: cart_items.cart_id -> CASCADE per SRS §6.4 (deleting a cart, e.g. after
-- checkout, clears its lines). carts.customer_id/restaurant_id stay RESTRICT, consistent
-- with the platform-wide rule that users/restaurants are never hard-deleted (BR-30).
-- cart_items.menu_item_id stays RESTRICT since menu items are soft-deleted only (BR-10).

CREATE TABLE carts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID NOT NULL,
    restaurant_id   UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_carts_customer FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_carts_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT,
    CONSTRAINT uk_carts_customer UNIQUE (customer_id)
);

CREATE TABLE cart_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id         UUID NOT NULL,
    menu_item_id    UUID NOT NULL,
    quantity        INTEGER NOT NULL,
    note            VARCHAR(500),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE RESTRICT,
    CONSTRAINT uk_cart_items_cart_menu_item UNIQUE (cart_id, menu_item_id),
    CONSTRAINT ck_cart_items_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
