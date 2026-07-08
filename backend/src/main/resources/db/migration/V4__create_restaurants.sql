-- Purpose: Restaurant storefronts (UC-R01). Column set mirrors restaurant.model.Restaurant
-- exactly (HIBERNATE_DDL_AUTO=validate) — this entity already carries more operational
-- fields (promotions, delivery economics, stats) than the SRS §6.1 summary table lists,
-- so this migration matches the code, not the SRS table verbatim.
--
-- Enum strategy: `status` is VARCHAR + CHECK, consistent with V1's reasoning (BR-31).
-- `deleted_at` exists on the entity but restaurants are never actually hard/soft-deleted
-- by any use case in the SRS (moderation uses `status = SUSPENDED`); the column is kept
-- nullable for forward compatibility with the entity, simply unused today.

CREATE TABLE restaurants (
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                      VARCHAR(200) NOT NULL,
    address                   VARCHAR(500) NOT NULL,
    phone                     VARCHAR(20)  NOT NULL,
    email                     VARCHAR(100),
    description               TEXT,
    cuisine                   VARCHAR(100) NOT NULL,
    image_url                 VARCHAR(500),
    cover_image_url           VARCHAR(500),
    rating_avg                DOUBLE PRECISION NOT NULL DEFAULT 0,
    total_reviews             INTEGER NOT NULL DEFAULT 0,
    status                    VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_open                   BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured               BOOLEAN NOT NULL DEFAULT FALSE,
    opening_time              VARCHAR(10),
    closing_time              VARCHAR(10),
    opening_hours             VARCHAR(200),
    delivery_fee              BIGINT NOT NULL DEFAULT 0,
    minimum_order             BIGINT NOT NULL DEFAULT 0,
    free_delivery_threshold   BIGINT NOT NULL DEFAULT 0,
    estimated_delivery_time   INTEGER,
    max_delivery_distance     DOUBLE PRECISION NOT NULL DEFAULT 10.0,
    latitude                  DOUBLE PRECISION,
    longitude                 DOUBLE PRECISION,
    total_orders              INTEGER NOT NULL DEFAULT 0,
    order_count               INTEGER NOT NULL DEFAULT 0,
    average_order_value       BIGINT NOT NULL DEFAULT 0,
    promotion_text            VARCHAR(200),
    has_promotion             BOOLEAN NOT NULL DEFAULT FALSE,
    accepts_cash              BOOLEAN NOT NULL DEFAULT TRUE,
    accepts_card              BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id                  UUID NOT NULL,
    created_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at                TIMESTAMPTZ,

    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT uk_restaurants_name UNIQUE (name),
    CONSTRAINT uk_restaurants_phone UNIQUE (phone),
    CONSTRAINT ck_restaurants_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED')),
    CONSTRAINT ck_restaurants_rating_avg CHECK (rating_avg BETWEEN 0 AND 5),
    CONSTRAINT ck_restaurants_total_reviews CHECK (total_reviews >= 0),
    CONSTRAINT ck_restaurants_delivery_fee CHECK (delivery_fee >= 0),
    CONSTRAINT ck_restaurants_minimum_order CHECK (minimum_order >= 0),
    CONSTRAINT ck_restaurants_free_delivery_threshold CHECK (free_delivery_threshold >= 0),
    CONSTRAINT ck_restaurants_latitude CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_restaurants_longitude CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180)
);

CREATE INDEX idx_restaurant_status ON restaurants (status);
CREATE INDEX idx_restaurants_owner_id ON restaurants (owner_id);

-- NFR §11.1: listing/search queries filter by status and a lat/lng bounding box together.
CREATE INDEX idx_restaurants_status_lat_lng ON restaurants (status, latitude, longitude);

-- NFR §11.1: trigram index backs fuzzy/partial name search without a full PostGIS/H3 upgrade.
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_restaurants_name_trgm ON restaurants USING GIN (name gin_trgm_ops);
