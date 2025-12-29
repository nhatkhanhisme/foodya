package com.example.foodya.data.repository

import android.util.Log
import com.example.foodya.data.remote.RestaurantApi
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant
import com.example.foodya.domain.repository.RestaurantRepository
import com.example.foodya.util.toUserFriendlyMessage
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApi
) : RestaurantRepository {

    override suspend fun getRestaurants(
        keyword: String?,
        cuisine: String?,
        minRating: Double?,
        sortBy: String?,
        page: Int,
        size: Int
    ): Result<Pair<List<Restaurant>, Boolean>> {
        return try {
            val response = api.getRestaurants(keyword, cuisine, minRating, sortBy, page, size)
            Log.d("RestaurantRepositoryImpl", "API Response: ${response.content.size} restaurants, page ${response.number}")
            
            val restaurants = response.content.map { it.toDomain() }
            
            val hasMore = !response.last
            Result.success(Pair(restaurants, hasMore))
        } catch (e: Exception) {
            Log.e("RestaurantRepositoryImpl", "Error fetching restaurants", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getRestaurantById(restaurantId: String): Result<Restaurant> {
        return try {
            val dto = api.getRestaurantById(restaurantId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Log.e("RestaurantRepositoryImpl", "Error fetching restaurant by ID", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getPopularRestaurants(limit: Int): Result<List<Restaurant>> {
        return try {
            val response = api.getPopularRestaurants(limit)
            val restaurants = response.map { it.toDomain() }
            Result.success(restaurants)
        } catch (e: Exception) {
            Log.e("RestaurantRepositoryImpl", "Error fetching popular restaurants", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<Food>> {
        return try {
            val response = api.getMenuByRestaurantId(restaurantId)
            val menuItems = response.map { it.toDomain() }
            Result.success(menuItems)
        } catch (e: Exception) {
            Log.e("RestaurantRepositoryImpl", "Error fetching menu items", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
