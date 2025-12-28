package com.example.foodya.domain.usecase

import com.example.foodya.data.model.MenuItemRequest

/**
 * Use case for validating menu item creation/update requests
 */
class ValidateMenuItemUseCase {
    
    /**
     * Validates menu item request and returns error message if invalid
     * @return null if valid, error message if invalid
     */
    operator fun invoke(request: MenuItemRequest): String? {
        // Validate name
        if (request.name.isBlank()) {
            return "Menu item name is required"
        }
        if (request.name.length < 2) {
            return "Name must be at least 2 characters"
        }
        if (request.name.length > 100) {
            return "Name must not exceed 100 characters"
        }
        
        // Validate description (optional)
        if (!request.description.isNullOrBlank() && request.description.length > 500) {
            return "Description must not exceed 500 characters"
        }
        
        // Validate price
        if (request.price <= 0) {
            return "Price must be greater than 0"
        }
        if (request.price > 99999.99) {
            return "Price must be less than 100,000"
        }
        
        // Validate category
        if (request.category.isBlank()) {
            return "Category is required"
        }
        
        // Validate preparation time (if provided)
        if (request.preparationTime != null) {
            if (request.preparationTime < 1) {
                return "Preparation time must be at least 1 minute"
            }
            if (request.preparationTime > 300) {
                return "Preparation time must not exceed 300 minutes"
            }
        }
        
        // Validate calories (if provided)
        if (request.calories != null && request.calories < 0) {
            return "Calories cannot be negative"
        }
        
        return null // Valid
    }
}
