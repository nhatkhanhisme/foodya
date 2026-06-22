package com.foodya.foodya_backend.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Menu item details response")
public class MenuItemResponse {

    @Schema(description = "Menu item ID", example = "1")
    private UUID id;

    @Schema(description = "Restaurant ID", example = "1")
    private UUID restaurantId;

    @Schema(description = "Restaurant name", example = "The Italian Corner")
    private String restaurantName;

    @Schema(description = "Menu item name", example = "Margherita Pizza")
    private String name;

    @Schema(description = "Description", example = "Classic Italian pizza")
    private String description;

    @Schema(description = "Price", example = "12.99")
    private Double price;

    @Schema(description = "Image URL")
    private String imageUrl;

    @Schema(description = "Category", example = "Main Course")
    private String category;

    @Schema(description = "Is available?", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Is active? (soft delete)", example = "true")
    private Boolean isActive;

    @Schema(description = "Preparation time in minutes", example = "15")
    private Integer preparationTime;

    @Schema(description = "Calories", example = "800")
    private Integer calories;

    @Schema(description = "Is vegetarian?", example = "true")
    private Boolean isVegetarian;

    @Schema(description = "Is vegan?", example = "false")
    private Boolean isVegan;

    @Schema(description = "Is gluten-free?", example = "false")
    private Boolean isGlutenFree;

    @Schema(description = "Is spicy?", example = "false")
    private Boolean isSpicy;

    @Schema(description = "Total orders count", example = "45")
    private Integer orderCount;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;
}
