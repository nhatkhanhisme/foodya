package com.example.foodya.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val rating: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val imageUrl: String
)