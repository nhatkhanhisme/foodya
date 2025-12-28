package com.example.foodya.data.remote

import com.example.foodya.data.model.MenuItemResponse
import com.example.foodya.data.model.RestaurantResponse
import retrofit2.http.GET

interface RestaurantApi {
    @GET("restaurants")
    suspend fun getAllRestaurant(): List<RestaurantResponse>

    @GET("restaurants/{restaurantId}/menu-items")
    suspend fun getMenuByRestaurantId(restaurantId: String): List<MenuItemResponse>
}