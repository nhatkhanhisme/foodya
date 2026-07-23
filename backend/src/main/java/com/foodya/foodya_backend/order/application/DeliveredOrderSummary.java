package com.foodya.foodya_backend.order.application;

import java.time.Instant;
import java.util.UUID;

public record DeliveredOrderSummary(UUID restaurantId, Instant deliveredAt) {
}
