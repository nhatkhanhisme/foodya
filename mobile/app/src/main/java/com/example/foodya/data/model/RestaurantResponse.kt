package com.example.foodya.data.model

data class RestaurantResponse(
    val id: String,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val description: String,
    val cuisine: String,
    val rating: Double,
    val totalReviews: Int,
    val isOpen: Boolean,
    val isActive: Boolean,
    val imageUrl: String,
    val openingTime: String,
    val closingTime: String,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val createdAt: String,
    val updatedAt: String,
    val menuItemsCount: Int
)
