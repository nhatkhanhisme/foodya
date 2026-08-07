-- ordering module: com.foodya.foodya_backend.ordering.domain.{Order,OrderItem}

CREATE TABLE orders (
    id                          UUID PRIMARY KEY,
    customer_id                 UUID          NOT NULL REFERENCES users (id),
    restaurant_id                UUID         NOT NULL REFERENCES restaurants (id),
    shipper_id                  UUID,
    delivery_address_snapshot   VARCHAR(500),
    delivery_address_id         UUID,
    total                       BIGINT        NOT NULL,
    shipping_fee                BIGINT        NOT NULL DEFAULT 0,
    subtotal                    BIGINT        NOT NULL DEFAULT 0,
    total_items                 INTEGER       NOT NULL,
    status                      VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    order_date                  TIMESTAMPTZ   NOT NULL,
    cancel_reason                VARCHAR(500),
    order_notes                 VARCHAR(1000),
    distance_km                 NUMERIC(10,2),
    distance_source              VARCHAR(10),
    version                     INTEGER       NOT NULL DEFAULT 0,
    confirmed_at                TIMESTAMPTZ,
    picked_up_at                TIMESTAMPTZ,
    delivered_at                TIMESTAMPTZ,
    cancelled_at                TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_restaurant_id ON orders (restaurant_id);
CREATE INDEX idx_orders_status ON orders (status);

CREATE TABLE order_items (
    id                    UUID PRIMARY KEY,
    order_id              UUID         NOT NULL REFERENCES orders (id),
    menu_item_id          UUID         NOT NULL REFERENCES menu_items (id),
    quantity              INTEGER      NOT NULL,
    item_name_snapshot    VARCHAR(500) NOT NULL,
    item_price_snapshot   BIGINT       NOT NULL,
    subtotal              BIGINT       NOT NULL,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_menu_item_id ON order_items (menu_item_id);
