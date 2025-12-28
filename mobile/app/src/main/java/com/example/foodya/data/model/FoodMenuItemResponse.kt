package com.example.foodya.data.model

data class FoodMenuItemResponse(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean,
    val isActive: Boolean
)

data class CreateMenuItemRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)

data class UpdateMenuItemRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean
)
