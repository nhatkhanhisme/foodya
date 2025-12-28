package com.example.foodya.data.model

data class MerchantRestaurantResponse(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val rating: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val imageUrl: String,
    val ownerId: String,
    val isActive: Boolean
)

data class RegisterRestaurantRequest(
    val name: String,
    val address: String,
    val description: String,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val imageUrl: String
)
