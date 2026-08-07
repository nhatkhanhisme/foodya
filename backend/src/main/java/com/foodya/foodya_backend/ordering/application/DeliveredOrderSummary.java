package com.foodya.foodya_backend.ordering.application;

import java.time.Instant;
import java.util.UUID;

public record DeliveredOrderSummary(UUID restaurantId, Instant deliveredAt) {
}
