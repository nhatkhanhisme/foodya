package com.example.foodya.data.model

/**
 * Request DTO for creating/updating a restaurant
 * Matches backend RestaurantRequest.java
 */
data class RestaurantRequest(
    // Basic Information
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String? = null,
    val description: String? = null,
    val cuisine: String,
    
    // Media
    val imageUrl: String? = null,
    val coverImageUrl: String? = null,
    
    // Operating Hours
    val openingTime: String? = null, // Format: HH:mm
    val closingTime: String? = null, // Format: HH:mm
    val openingHours: String? = null,
    
    // Delivery Information
    val minimumOrder: Double? = null,
    val maxDeliveryDistance: Double? = null,
    
    // Status
    val isOpen: Boolean? = true
)

/**
 * Request DTO for creating/updating a menu item
 * Matches backend MenuItemRequest.java
 */
data class MenuItemRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val imageUrl: String? = null,
    val category: String,
    val preparationTime: Int? = null,
    val calories: Int? = null,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isSpicy: Boolean = false,
    val isAvailable: Boolean = true
)
