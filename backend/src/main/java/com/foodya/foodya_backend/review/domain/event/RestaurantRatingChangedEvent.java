package com.foodya.foodya_backend.review.domain.event;

import java.util.UUID;

/**
 * Carries the already-computed new average/count so catalog's listener never
 * has to query review's own table (which would be a listener that reaches
 * back into the module that raised it — the same shape of cycle that
 * RestaurantPopularityService/OrderService needed @Lazy to avoid).
 */
public record RestaurantRatingChangedEvent(UUID restaurantId, double newAverageRating, int newTotalReviews) {
}
