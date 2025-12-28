package com.example.foodya.domain.repository

import com.example.foodya.data.model.MenuItemRequest
import com.example.foodya.data.model.RestaurantRequest
import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.model.OrderWithDetails
import com.example.foodya.domain.model.enums.OrderStatus

interface MerchantRepository {
    // ==================== RESTAURANT MANAGEMENT ====================
    
    /**
     * Get all restaurants owned by the current merchant
     */
    suspend fun getMyRestaurants(): Result<List<MerchantRestaurant>>
    
    /**
     * Create a new restaurant
     * @param request Restaurant creation request with all required fields
     */
    suspend fun createRestaurant(request: RestaurantRequest): Result<MerchantRestaurant>
    
    /**
     * Update restaurant information
     */
    suspend fun updateRestaurant(
        restaurantId: String,
        request: RestaurantRequest
    ): Result<MerchantRestaurant>
    
    /**
     * Toggle restaurant open/close status
     */
    suspend fun toggleRestaurantStatus(restaurantId: String): Result<MerchantRestaurant>

    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * Get all orders for a specific restaurant
     */
    suspend fun getOrdersByRestaurant(restaurantId: String): Result<List<OrderWithDetails>>
    
    /**
     * Update order status with optional cancel reason
     */
    suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        cancelReason: String? = null
    ): Result<OrderWithDetails>

    // ==================== MENU MANAGEMENT ====================
    
    /**
     * Get all menu items for a restaurant
     */
    suspend fun getMenuItems(restaurantId: String): Result<List<FoodMenuItem>>
    
    /**
     * Create a new menu item
     * @param restaurantId Restaurant ID
     * @param request Menu item creation request
     */
    suspend fun createMenuItem(
        restaurantId: String,
        request: MenuItemRequest
    ): Result<FoodMenuItem>
    
    /**
     * Update menu item
     */
    suspend fun updateMenuItem(
        restaurantId: String,
        menuItemId: String,
        request: MenuItemRequest
    ): Result<FoodMenuItem>
    
    /**
     * Delete (soft delete) menu item
     */
    suspend fun deleteMenuItem(
        restaurantId: String,
        menuItemId: String
    ): Result<Unit>
    
    /**
     * Toggle menu item availability
     */
    suspend fun toggleMenuItemAvailability(
        restaurantId: String,
        menuItemId: String
    ): Result<FoodMenuItem>
}
