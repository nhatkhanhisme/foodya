package com.foodya.foodya_backend.restaurant.dto;

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
    private UUID id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String cuisine;
    private Double rating;
    private Integer totalReviews;
    private Boolean isOpen;
    private Boolean isActive;
    private String imageUrl;
    private String openingTime;
    private String closingTime;
    private Double deliveryFee;
    private Integer estimatedDeliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer menuItemsCount; // Optional: số lượng món ăn
}
