package com.example.foodya.domain.model

data class FoodMenuItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String = "",
    val price: Double,
    val imageUrl: String = "",
    val category: String,
    val isAvailable: Boolean = true,
    val isActive: Boolean = true,
    val preparationTime: Int? = null,
    val calories: Int? = null,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isSpicy: Boolean = false
)
