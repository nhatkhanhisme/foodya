-- Purpose: Sellable items (UC-R03). Column set mirrors restaurant.model.MenuItem
-- exactly (HIBERNATE_DDL_AUTO=validate) — nutritional/operational fields beyond the
-- SRS §6.1 summary (calories, prep time, dietary flags, popularity counter) are kept
-- since the entity already has them.
--
-- BR-10: `is_deleted` is the soft-delete flag — items referenced by historical
-- OrderItem rows are hidden, never hard-deleted, so order_items.menu_item_id always
-- resolves. category_id/restaurant_id are NOT NULL per SRS §6.3 — every menu item
-- belongs to exactly one category of exactly one restaurant.

CREATE TABLE menu_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    price               BIGINT NOT NULL,
    image_url           VARCHAR(255),
    category_id         UUID NOT NULL,
    restaurant_id       UUID NOT NULL,
    is_available        BOOLEAN NOT NULL DEFAULT TRUE,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted          BOOLEAN NOT NULL DEFAULT FALSE,
    preparation_time    INTEGER,
    calories            INTEGER,
    is_vegetarian       BOOLEAN,
    is_vegan            BOOLEAN,
    is_gluten_free      BOOLEAN,
    is_spicy            BOOLEAN,
    order_count         INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_menu_items_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT,
    CONSTRAINT ck_menu_items_price CHECK (price >= 0),
    CONSTRAINT ck_menu_items_order_count CHECK (order_count >= 0)
);

CREATE INDEX idx_menu_items_restaurant_id ON menu_items (restaurant_id);
CREATE INDEX idx_menu_items_category_id ON menu_items (category_id);
CREATE INDEX idx_food_name ON menu_items (name);
