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

    override suspend fun getAllRestaurant(): Result<List<Restaurant>> {
        return try {
            val response = api.getAllRestaurant()
            Log.d("RestaurantRepositoryImpl", "Response: $response")
            val result = response.map{it ->
                Restaurant(
                    id = it.id,
                    name = it.name,
                    address = it.address,
                    description = it.address,
                    rating = it.rating,
                    deliveryFee = it.deliveryFee,
                    estimatedDeliveryTime = it.estimatedDeliveryTime,
                    imageUrl = it.imageUrl
                )
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<Food>> {
        return try {
            val response = api.getMenuByRestaurantId(restaurantId = restaurantId)
            val result = response.map{it ->
                Food(
                    id = it.id,
                    restaurantId = it.restaurantId,
                    restaurantName = it.restaurantName,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    category = it.category,
                    isAvailable = it.isAvailable,
                    isActive = it.isActive
                )
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}