package com.example.foodya.domain.repository

import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.model.OrderWithDetails
import com.example.foodya.domain.model.enums.OrderStatus

interface MerchantRepository {
    // Restaurant Management
    suspend fun getMyRestaurants(): Result<List<MerchantRestaurant>>
    suspend fun registerRestaurant(
        name: String,
        address: String,
        description: String,
        deliveryFee: Double,
        estimatedDeliveryTime: Int,
        imageUrl: String
    ): Result<MerchantRestaurant>

    // Order Management
    suspend fun getOrdersByRestaurant(restaurantId: String): Result<List<OrderWithDetails>>
    suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        cancelReason: String? = null
    ): Result<OrderWithDetails>

    // Menu Management
    suspend fun getMenuItems(restaurantId: String): Result<List<FoodMenuItem>>
    suspend fun createMenuItem(
        restaurantId: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        category: String
    ): Result<FoodMenuItem>
    suspend fun updateMenuItem(
        menuItemId: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        category: String,
        isAvailable: Boolean
    ): Result<FoodMenuItem>
    suspend fun deleteMenuItem(menuItemId: String): Result<Unit>
}
