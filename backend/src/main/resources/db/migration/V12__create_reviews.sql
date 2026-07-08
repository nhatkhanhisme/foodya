-- Purpose: Customer ratings/comments on a delivered order (UC-C10). No JPA entity
-- exists yet, so this follows the SRS domain model (§6.1) directly.
--
-- BR-15: a review may be created only once per order, and only when the order is
-- DELIVERED. The "only when DELIVERED" half is a runtime check (service layer, since it
-- depends on Order.status at submission time), but "only once per order" is a structural
-- invariant — uk_reviews_order enforces it at the DB level as well, so a race between two
-- concurrent submit-review requests can't both succeed.
-- Restaurant.rating_avg (BR-16) is recalculated by the service layer on every insert;
-- it is not derived via a DB trigger to keep that logic in one place (RestaurantService).

CREATE TABLE reviews (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL,
    customer_id     UUID NOT NULL,
    restaurant_id   UUID NOT NULL,
    rating          SMALLINT NOT NULL,
    comment         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT,
    CONSTRAINT uk_reviews_order UNIQUE (order_id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_customer_id ON reviews (customer_id);
CREATE INDEX idx_reviews_restaurant_created_at ON reviews (restaurant_id, created_at DESC);
