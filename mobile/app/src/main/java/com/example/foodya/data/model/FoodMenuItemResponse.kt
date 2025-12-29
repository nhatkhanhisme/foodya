package com.example.foodya.data.model

import com.example.foodya.domain.model.FoodMenuItem
import com.google.gson.annotations.SerializedName

data class FoodMenuItemResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("restaurantId")
    val restaurantId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("isAvailable")
    val isAvailable: Boolean = true,
    
    @SerializedName("isActive")
    val isActive: Boolean = true,
    
    @SerializedName("preparationTime")
    val preparationTime: Int? = null,
    
    @SerializedName("calories")
    val calories: Int? = null,
    
    @SerializedName("isVegetarian")
    val isVegetarian: Boolean = false,
    
    @SerializedName("isVegan")
    val isVegan: Boolean = false,
    
    @SerializedName("isGlutenFree")
    val isGlutenFree: Boolean = false,
    
    @SerializedName("isSpicy")
    val isSpicy: Boolean = false
) {
    fun toDomain(): FoodMenuItem {
        return FoodMenuItem(
            id = id,
            restaurantId = restaurantId,
            name = name,
            description = description ?: "",
            price = price,
            imageUrl = imageUrl ?: "",
            category = category,
            isAvailable = isAvailable,
            isActive = isActive,
            preparationTime = preparationTime,
            calories = calories,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            isSpicy = isSpicy
        )
    }
}

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
