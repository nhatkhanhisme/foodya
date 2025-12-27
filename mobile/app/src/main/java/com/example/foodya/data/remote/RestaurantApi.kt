package com.example.foodya.data.remote

import com.example.foodya.data.model.MenuItemResponse
import retrofit2.http.GET

interface RestaurantApi {
    @GET
    suspend fun getMenuByRestaurantId(restaurantId: String): List<MenuItemResponse>
}