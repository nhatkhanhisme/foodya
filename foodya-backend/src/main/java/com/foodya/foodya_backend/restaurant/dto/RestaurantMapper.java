package com.foodya.foodya_backend.restaurant.dto;

import com.foodya.foodya_backend.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {

    // Transform Restaurant entity to RestaurantResponse DTO
    public RestaurantResponse toRestaurantResponse(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .rating(restaurant.getRating())
                .totalReviews(restaurant.getTotalReviews())
                .isOpen(restaurant.getIsOpen())
                .isActive(restaurant.getIsActive())
                .imageUrl(restaurant.getImageUrl())
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .deliveryFee(restaurant.getDeliveryFee())
                .estimatedDeliveryTime(restaurant.getEstimatedDeliveryTime())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .menuItemsCount(restaurant.getMenuItems() != null ? restaurant.getMenuItems().size() : 0)
                .build();
    }

    // Transform list of Restaurant entities to list of RestaurantResponse DTOs
    public List<RestaurantResponse> toRestaurantResponseList(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(this::toRestaurantResponse)
                .collect(Collectors.toList());
    }
}
