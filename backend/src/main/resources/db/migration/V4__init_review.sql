-- review module: com.foodya.foodya_backend.review.domain.Review

CREATE TABLE reviews (
    id            UUID PRIMARY KEY,
    order_id      UUID         NOT NULL REFERENCES orders (id),
    customer_id   UUID         NOT NULL REFERENCES users (id),
    restaurant_id UUID         NOT NULL REFERENCES restaurants (id),
    rating        INTEGER      NOT NULL,
    comment       VARCHAR(1000),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_reviews_order_id UNIQUE (order_id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_restaurant_id ON reviews (restaurant_id);
CREATE INDEX idx_reviews_customer_id ON reviews (customer_id);
