package com.foodya.foodya_backend.restaurant.dto;

import com.foodya.foodya_backend.restaurant.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    // Basic Information
    private UUID id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String cuisine;

    // Media
    private String imageUrl;
    private String coverImageUrl;

    // Rating & Reviews
    private Double rating;
    private Integer totalReviews;

    // Status
    private Boolean isOpen;
    private Boolean isActive;
    private Boolean isVerified;
    private Boolean isFeatured;

    // Operating Hours
    private String openingTime;
    private String closingTime;
    private String openingHours;

    // Delivery Information
    private Double deliveryFee;
    private Double minimumOrder;
    private Double freeDeliveryThreshold;
    private Integer estimatedDeliveryTime;
    private Double maxDeliveryDistance;

    // Location
    private Double latitude;
    private Double longitude;

    // Statistics
    private Integer totalOrders;
    private Integer orderCount;
    private Double averageOrderValue;

    // Promo & Features
    private String promotionText;
    private Boolean hasPromotion;
    private Boolean acceptsCash;
    private Boolean acceptsCard;

    // Metadata
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Integer menuItemsCount;
    private Boolean isCurrentlyOpen;

    /**
     * Convert Restaurant entity to RestaurantResponse DTO
     */
    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        return RestaurantResponse.builder()
                // Basic Information
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())

                // Media
                .imageUrl(restaurant.getImageUrl())
                .coverImageUrl(restaurant. getCoverImageUrl())

                // Rating & Reviews
                . rating(restaurant.getRating())
                .totalReviews(restaurant.getTotalReviews())

                // Status
                . isOpen(restaurant.getIsOpen())
                .isActive(restaurant.getIsActive())
                .isVerified(restaurant. getIsVerified())
                .isFeatured(restaurant.getIsFeatured())

                // Operating Hours
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .openingHours(restaurant.getOpeningHours())

                // Delivery Information
                .deliveryFee(restaurant.getDeliveryFee())
                .minimumOrder(restaurant.getMinimumOrder())
                .freeDeliveryThreshold(restaurant.getFreeDeliveryThreshold())
                .estimatedDeliveryTime(restaurant.getEstimatedDeliveryTime())
                .maxDeliveryDistance(restaurant.getMaxDeliveryDistance())

                // Location
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())

                // Statistics
                .totalOrders(restaurant.getTotalOrders())
                .orderCount(restaurant.getOrderCount())
                .averageOrderValue(restaurant.getAverageOrderValue())

                // Promo & Features
                .promotionText(restaurant.getPromotionText())
                .hasPromotion(restaurant.getHasPromotion())
                .acceptsCash(restaurant.getAcceptsCash())
                .acceptsCard(restaurant.getAcceptsCard())

                // Metadata
                .ownerId(restaurant.getOwnerId())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())

                // Computed fields
                .menuItemsCount(restaurant.getMenuItems() != null ? restaurant.getMenuItems().size() : 0)
                .isCurrentlyOpen(restaurant.isCurrentlyOpen())

                . build();
    }
}
