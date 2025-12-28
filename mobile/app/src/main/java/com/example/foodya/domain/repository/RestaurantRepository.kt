package com.example.foodya.domain.repository

import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant

interface RestaurantRepository {
    suspend fun getAllRestaurant(): Result<List<Restaurant>>

    suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<Food>>
}