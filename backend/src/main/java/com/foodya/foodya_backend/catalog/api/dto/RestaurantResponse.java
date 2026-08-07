package com.foodya.foodya_backend.catalog.api.dto;

import com.foodya.foodya_backend.catalog.domain.Restaurant;
import com.foodya.foodya_backend.catalog.domain.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private UUID id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String cuisine;

    private String imageUrl;
    private String coverImageUrl;

    private Double rating;
    private Integer totalReviews;

    private RestaurantStatus status;
    private String rejectionReason;
    private Boolean isOpen;
    private Boolean isFeatured;

    private String openingTime;
    private String closingTime;
    private String openingHours;

    private Long deliveryFee;
    private Long minimumOrder;
    private Long freeDeliveryThreshold;
    private Integer estimatedDeliveryTime;
    private Double maxDeliveryDistance;

    private Double latitude;
    private Double longitude;

    private Integer totalOrders;
    private Integer orderCount;
    private Long averageOrderValue;

    private String promotionText;
    private Boolean hasPromotion;
    private Boolean acceptsCash;
    private Boolean acceptsCard;

    private UUID ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    private Integer menuItemsCount;
    private Boolean isCurrentlyOpen;

    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .imageUrl(restaurant.getImageUrl())
                .coverImageUrl(restaurant.getCoverImageUrl())
                .rating(restaurant.getRating())
                .totalReviews(restaurant.getTotalReviews())
                .status(restaurant.getStatus())
                .rejectionReason(restaurant.getRejectionReason())
                .isOpen(restaurant.getIsOpen())
                .isFeatured(restaurant.getIsFeatured())
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .openingHours(restaurant.getOpeningHours())
                .deliveryFee(restaurant.getDeliveryFee())
                .minimumOrder(restaurant.getMinimumOrder())
                .freeDeliveryThreshold(restaurant.getFreeDeliveryThreshold())
                .estimatedDeliveryTime(restaurant.getEstimatedDeliveryTime())
                .maxDeliveryDistance(restaurant.getMaxDeliveryDistance())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .totalOrders(restaurant.getTotalOrders())
                .orderCount(restaurant.getOrderCount())
                .averageOrderValue(restaurant.getAverageOrderValue())
                .promotionText(restaurant.getPromotionText())
                .hasPromotion(restaurant.getHasPromotion())
                .acceptsCash(restaurant.getAcceptsCash())
                .acceptsCard(restaurant.getAcceptsCard())
                .ownerId(restaurant.getOwnerId())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .menuItemsCount(restaurant.getMenuItems() != null ? restaurant.getMenuItems().size() : 0)
                .isCurrentlyOpen(restaurant.isCurrentlyOpen())
                .build();
    }
}
