package com.example.foodya.domain.model

data class Food(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean = true,
    val isActive: Boolean = true,
)