package com.foodya.foodya_backend.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a menu item")
public class MenuItemRequest {

    @NotBlank(message = "Menu item name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the menu item", example = "Margherita Pizza")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Detailed description of the menu item", example = "Classic Italian pizza with tomato and mozzarella")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price must be less than 100000")
    @Schema(description = "Price of the menu item", example = "12.99")
    private Double price;

    @Schema(description = "Image URL of the menu item", example = "https://example.com/pizza.jpg")
    private String imageUrl;

    @NotBlank(message = "Category is required")
    @Schema(description = "Category of the menu item", example = "Main Course",
            allowableValues = {"Appetizer", "Main Course", "Dessert", "Beverage", "Side Dish"})
    private String category;

    @Schema(description = "Preparation time in minutes", example = "15")
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 300, message = "Preparation time cannot exceed 300 minutes")
    private Integer preparationTime;

    @Schema(description = "Calories", example = "800")
    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    @Schema(description = "Is vegetarian?", example = "true")
    @Builder.Default
    private Boolean isVegetarian = false;

    @Schema(description = "Is vegan?", example = "false")
    @Builder.Default
    private Boolean isVegan = false;

    @Schema(description = "Is gluten-free?", example = "false")
    @Builder.Default
    private Boolean isGlutenFree = false;

    @Schema(description = "Is spicy?", example = "false")
    @Builder.Default
    private Boolean isSpicy = false;

    @Schema(description = "Is available for order?", example = "true")
    @Builder.Default
    private Boolean isAvailable = true;
}
