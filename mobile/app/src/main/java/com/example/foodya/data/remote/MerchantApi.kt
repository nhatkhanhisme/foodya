package com.example.foodya.data.remote

import com.example.foodya.data.model.*
import retrofit2.http.*

interface MerchantApi {
    // Restaurant Management
    @GET("merchant/restaurants")
    suspend fun getMyRestaurants(): List<MerchantRestaurantResponse>

    @POST("merchant/restaurants")
    suspend fun registerRestaurant(
        @Body request: RegisterRestaurantRequest
    ): MerchantRestaurantResponse

    // Order Management
    @GET("merchant/restaurants/{restaurantId}/orders")
    suspend fun getOrdersByRestaurant(
        @Path("restaurantId") restaurantId: String
    ): List<OrderResponse>

    @PUT("merchant/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): OrderResponse

    // Menu Management
    @GET("merchant/restaurants/{restaurantId}/menu-items")
    suspend fun getMenuItems(
        @Path("restaurantId") restaurantId: String
    ): List<FoodMenuItemResponse>

    @POST("merchant/restaurants/{restaurantId}/menu-items")
    suspend fun createMenuItem(
        @Path("restaurantId") restaurantId: String,
        @Body request: CreateMenuItemRequest
    ): FoodMenuItemResponse

    @PUT("merchant/menu-items/{menuItemId}")
    suspend fun updateMenuItem(
        @Path("menuItemId") menuItemId: String,
        @Body request: UpdateMenuItemRequest
    ): FoodMenuItemResponse

    @DELETE("merchant/menu-items/{menuItemId}")
    suspend fun deleteMenuItem(
        @Path("menuItemId") menuItemId: String
    )
}
