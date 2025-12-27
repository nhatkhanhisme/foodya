package com.example.foodya.data.repository

import com.example.foodya.data.model.MenuItemResponse
import com.example.foodya.data.remote.RestaurantApi
import com.example.foodya.domain.repository.RestaurantRepository
import com.example.foodya.util.toUserFriendlyMessage
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApi
) : RestaurantRepository {

    override suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<MenuItemResponse>> {
        return try {
            val response = api.getMenuByRestaurantId(restaurantId = restaurantId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}