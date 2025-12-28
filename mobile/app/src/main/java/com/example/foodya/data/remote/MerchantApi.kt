package com.example.foodya.data.remote

import com.example.foodya.data.model.*
import retrofit2.http.*

interface MerchantApi {
    // ==================== RESTAURANT MANAGEMENT ====================
    
    /**
     * Get all restaurants owned by current merchant
     */
    @GET("merchant/restaurants/me")
    suspend fun getMyRestaurants(): List<MerchantRestaurantResponse>

    /**
     * Create a new restaurant (POST /api/v1/merchant/restaurants)
     */
    @POST("merchant/restaurants")
    suspend fun createRestaurant(
        @Body request: RestaurantRequest
    ): MerchantRestaurantResponse

    /**
     * Update restaurant information (PUT /api/v1/merchant/restaurants/{id})
     */
    @PUT("merchant/restaurants/{id}")
    suspend fun updateRestaurant(
        @Path("id") restaurantId: String,
        @Body request: RestaurantRequest
    ): MerchantRestaurantResponse

    /**
     * Toggle restaurant open/close status
     */
    @PATCH("merchant/restaurants/{id}/toggle-status")
    suspend fun toggleRestaurantStatus(
        @Path("id") restaurantId: String
    ): MerchantRestaurantResponse

    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * Get all orders for a specific restaurant
     */
    @GET("merchant/restaurants/{restaurantId}/orders")
    suspend fun getOrdersByRestaurant(
        @Path("restaurantId") restaurantId: String
    ): List<OrderResponse>

    /**
     * Update order status
     */
    @PUT("merchant/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): OrderResponse

    // ==================== MENU MANAGEMENT ====================
    
    /**
     * Get menu items for a restaurant
     */
    @GET("merchant/restaurants/{restaurantId}/menu-items")
    suspend fun getMenuItems(
        @Path("restaurantId") restaurantId: String
    ): List<FoodMenuItemResponse>

    /**
     * Create a new menu item (POST /api/v1/merchant/restaurants/{restaurantId}/menu-items)
     */
    @POST("merchant/restaurants/{restaurantId}/menu-items")
    suspend fun createMenuItem(
        @Path("restaurantId") restaurantId: String,
        @Body request: MenuItemRequest
    ): FoodMenuItemResponse

    /**
     * Update menu item (PUT /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId})
     */
    @PUT("merchant/restaurants/{restaurantId}/menu-items/{menuItemId}")
    suspend fun updateMenuItem(
        @Path("restaurantId") restaurantId: String,
        @Path("menuItemId") menuItemId: String,
        @Body request: MenuItemRequest
    ): FoodMenuItemResponse

    /**
     * Delete (soft delete) menu item
     */
    @DELETE("merchant/restaurants/{restaurantId}/menu-items/{menuItemId}")
    suspend fun deleteMenuItem(
        @Path("restaurantId") restaurantId: String,
        @Path("menuItemId") menuItemId: String
    )

    /**
     * Toggle menu item availability
     */
    @PATCH("merchant/restaurants/{restaurantId}/menu-items/{menuItemId}/toggle-availability")
    suspend fun toggleMenuItemAvailability(
        @Path("restaurantId") restaurantId: String,
        @Path("menuItemId") menuItemId: String
    ): FoodMenuItemResponse
}
