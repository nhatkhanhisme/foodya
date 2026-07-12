package com.foodya.foodya_backend.order.event;

import java.util.UUID;

public record OrderDeliveredEvent(UUID orderId, UUID restaurantId) {
}
