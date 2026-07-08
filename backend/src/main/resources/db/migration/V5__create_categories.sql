-- Purpose: Menu categories (UC-R02), scoped to one restaurant with a manual display
-- order. Table is named `categories` (plural), matching restaurant.model.Category's
-- @Table annotation and this project's general naming convention.
--
-- uk_categories_restaurant_name is a normalization addition beyond what the entity
-- enforces: two categories with the identical name in the same restaurant would be
-- confusing in the UI and serves no business purpose, so it's blocked at the DB level.

CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    restaurant_id   UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_categories_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT,
    CONSTRAINT uk_categories_restaurant_name UNIQUE (restaurant_id, name)
);

CREATE INDEX idx_categories_restaurant_id ON categories (restaurant_id);
