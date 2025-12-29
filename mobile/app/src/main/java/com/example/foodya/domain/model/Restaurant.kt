package com.example.foodya.domain.model

data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String? = null,
    val description: String? = null,
    val cuisine: String,
    val imageUrl: String? = null,
    val coverImageUrl: String? = null,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val isOpen: Boolean = true,
    val isActive: Boolean = true,
    val openingTime: String? = null,
    val closingTime: String? = null,
    val openingHours: String? = null,
    val deliveryFee: Double = 0.0,
    val minimumOrder: Double? = null,
    val maxDeliveryDistance: Double? = null,
    val estimatedDeliveryTime: Int = 30,
    val menuItemsCount: Int = 0
)