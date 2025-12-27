package com.example.foodya.domain.repository

import com.example.foodya.data.model.MenuItemResponse

interface RestaurantRepository {
    suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<MenuItemResponse>>
}