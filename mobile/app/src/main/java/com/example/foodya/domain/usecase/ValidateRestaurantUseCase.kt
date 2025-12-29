package com.example.foodya.domain.usecase

import com.example.foodya.data.model.RestaurantRequest

/**
 * Use case for validating restaurant creation/update requests
 */
class ValidateRestaurantUseCase {
    
    /**
     * Validates restaurant request and returns error message if invalid
     * @return null if valid, error message if invalid
     */
    operator fun invoke(request: RestaurantRequest): String? {
        // Validate name
        if (request.name.isBlank()) {
            return "Restaurant name is required"
        }
        if (request.name.length < 2) {
            return "Restaurant name must be at least 2 characters"
        }
        if (request.name.length > 200) {
            return "Restaurant name must not exceed 200 characters"
        }
        
        // Validate address
        if (request.address.isBlank()) {
            return "Address is required"
        }
        if (request.address.length > 500) {
            return "Address must not exceed 500 characters"
        }
        
        // Validate phone number
        if (request.phoneNumber.isBlank()) {
            return "Phone number is required"
        }
        if (!request.phoneNumber.matches(Regex("^\\+?[1-9]\\d{1,14}$"))) {
            return "Invalid phone number format"
        }
        
        // Validate email (optional)
        if (!request.email.isNullOrBlank()) {
            if (!request.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
                return "Invalid email format"
            }
        }
        
        // Validate cuisine
        if (request.cuisine.isBlank()) {
            return "Cuisine type is required"
        }
        
        // Validate opening/closing times (if provided)
        if (request.openingTime != null && !request.openingTime.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
            return "Invalid opening time format. Use HH:mm"
        }
        if (request.closingTime != null && !request.closingTime.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
            return "Invalid closing time format. Use HH:mm"
        }
        
        // Validate minimum order (if provided)
        if (request.minimumOrder != null && request.minimumOrder < 0) {
            return "Minimum order must be non-negative"
        }
        
        // Validate max delivery distance (if provided)
        if (request.maxDeliveryDistance != null) {
            if (request.maxDeliveryDistance < 0) {
                return "Max delivery distance must be non-negative"
            }
            if (request.maxDeliveryDistance > 100) {
                return "Max delivery distance must not exceed 100 km"
            }
        }
        
        return null // Valid
    }
}
