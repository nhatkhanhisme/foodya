package com.example.foodya.data.model

data class MenuItemResponse(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean,
    val isActive: Boolean,
    val preparationTime: Int,
    val calories: Int,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isSpicy: Boolean,
    val orderCount: Int,
    val createdAt: String,
    val updatedAt: String
)