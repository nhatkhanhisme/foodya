package com.example.foodya.domain.model

data class MerchantRestaurant(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val rating: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val imageUrl: String,
    val ownerId: String,
    val isActive: Boolean = true
)
