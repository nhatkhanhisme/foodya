package com.example.foodya.data.model

import com.example.foodya.domain.model.Food
import com.google.gson.annotations.SerializedName

data class MenuItemResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("restaurantId")
    val restaurantId: String,
    
    @SerializedName("restaurantName")
    val restaurantName: String,
    
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
    val isSpicy: Boolean = false,
    
    @SerializedName("orderCount")
    val orderCount: Int? = 0,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    fun toDomain(): Food {
        return Food(
            id = id,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
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
            isSpicy = isSpicy,
            orderCount = orderCount ?: 0
        )
    }
}