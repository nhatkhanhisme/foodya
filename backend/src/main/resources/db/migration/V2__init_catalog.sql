-- catalog module: com.foodya.foodya_backend.catalog.domain.{Restaurant,Category,MenuItem}

CREATE TABLE restaurants (
    id                          UUID PRIMARY KEY,
    name                        VARCHAR(200)     NOT NULL,
    address                     VARCHAR(500)     NOT NULL,
    phone                       VARCHAR(20)      NOT NULL,
    email                       VARCHAR(100),
    description                 TEXT,
    cuisine                     VARCHAR(100)     NOT NULL,
    image_url                   VARCHAR(500),
    cover_image_url             VARCHAR(500),
    rating_avg                  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_reviews               INTEGER          NOT NULL DEFAULT 0,
    status                      VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    rejection_reason            VARCHAR(500),
    is_open                     BOOLEAN          NOT NULL DEFAULT TRUE,
    is_featured                 BOOLEAN          NOT NULL DEFAULT FALSE,
    opening_time                VARCHAR(10),
    closing_time                VARCHAR(10),
    opening_hours               VARCHAR(200),
    delivery_fee                BIGINT           NOT NULL DEFAULT 0,
    minimum_order               BIGINT           NOT NULL DEFAULT 0,
    free_delivery_threshold     BIGINT           NOT NULL DEFAULT 0,
    estimated_delivery_time     INTEGER,
    max_delivery_distance       DOUBLE PRECISION NOT NULL DEFAULT 10.0,
    latitude                    DOUBLE PRECISION,
    longitude                   DOUBLE PRECISION,
    total_orders                INTEGER          NOT NULL DEFAULT 0,
    order_count                 INTEGER          NOT NULL DEFAULT 0,
    average_order_value         BIGINT           NOT NULL DEFAULT 0,
    promotion_text               VARCHAR(200),
    has_promotion               BOOLEAN          NOT NULL DEFAULT FALSE,
    accepts_cash                BOOLEAN          NOT NULL DEFAULT TRUE,
    accepts_card                BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ      NOT NULL DEFAULT now(),
    deleted_at                  TIMESTAMPTZ,
    owner_id                    UUID             NOT NULL,

    CONSTRAINT uq_restaurants_name UNIQUE (name),
    CONSTRAINT uq_restaurants_phone UNIQUE (phone)
);

CREATE INDEX idx_restaurants_owner_id ON restaurants (owner_id);
CREATE INDEX idx_restaurants_status ON restaurants (status);

CREATE TABLE categories (
    id             UUID PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    display_order  INTEGER      NOT NULL DEFAULT 0,
    restaurant_id  UUID         NOT NULL REFERENCES restaurants (id)
);

CREATE INDEX idx_categories_restaurant_id ON categories (restaurant_id);

CREATE TABLE menu_items (
    id                 UUID PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    price              BIGINT       NOT NULL,
    image_url          VARCHAR(255),
    category_id        UUID         NOT NULL REFERENCES categories (id),
    is_available       BOOLEAN      NOT NULL DEFAULT TRUE,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    is_deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    preparation_time   INTEGER,
    calories           INTEGER,
    is_vegetarian      BOOLEAN,
    is_vegan           BOOLEAN,
    is_gluten_free     BOOLEAN,
    is_spicy           BOOLEAN,
    order_count        INTEGER      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    restaurant_id      UUID         NOT NULL REFERENCES restaurants (id)
);

CREATE INDEX idx_menu_items_restaurant_id ON menu_items (restaurant_id);
CREATE INDEX idx_menu_items_category_id ON menu_items (category_id);
