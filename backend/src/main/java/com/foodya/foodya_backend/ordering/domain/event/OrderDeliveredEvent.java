package com.foodya.foodya_backend.ordering.domain.event;

import java.util.UUID;

public record OrderDeliveredEvent(UUID orderId, UUID restaurantId) {
}
