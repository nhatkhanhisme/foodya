package com.example.foodya.data.repository

import android.util.Log
import com.example.foodya.data.model.*
import com.example.foodya.data.remote.MerchantApi
import com.example.foodya.domain.model.*
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.domain.repository.MerchantRepository
import com.example.foodya.util.toUserFriendlyMessage
import javax.inject.Inject

class MerchantRepositoryImpl @Inject constructor(
    private val api: MerchantApi
) : MerchantRepository {
    
    // ==================== RESTAURANT MANAGEMENT ====================
    
    override suspend fun getMyRestaurants(): Result<List<MerchantRestaurant>> {
        return try {
            val response = api.getMyRestaurants()
            Log.d("MerchantRepository", "Fetched ${response.size} restaurants")
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error fetching restaurants", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun createRestaurant(request: RestaurantRequest): Result<MerchantRestaurant> {
        return try {
            // Validate request
            require(request.name.isNotBlank()) { "Restaurant name is required" }
            require(request.address.isNotBlank()) { "Address is required" }
            require(request.cuisine.isNotBlank()) { "Cuisine type is required" }
            
            val response = api.createRestaurant(request)
            Log.d("MerchantRepository", "Created restaurant: ${response.name}")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error creating restaurant", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun updateRestaurant(
        restaurantId: String,
        request: RestaurantRequest
    ): Result<MerchantRestaurant> {
        return try {
            val response = api.updateRestaurant(restaurantId, request)
            Log.d("MerchantRepository", "Updated restaurant: $restaurantId")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error updating restaurant", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun toggleRestaurantStatus(restaurantId: String): Result<MerchantRestaurant> {
        return try {
            val response = api.toggleRestaurantStatus(restaurantId)
            Log.d("MerchantRepository", "Toggled restaurant status: $restaurantId")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error toggling restaurant status", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    // ==================== ORDER MANAGEMENT ====================

    override suspend fun getOrdersByRestaurant(restaurantId: String): Result<List<OrderWithDetails>> {
        return try {
            val response = api.getOrdersByRestaurant(restaurantId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error fetching orders", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        cancelReason: String?
    ): Result<OrderWithDetails> {
        return try {
            val request = UpdateOrderStatusRequest(
                status = status.name,
                cancelReason = cancelReason
            )
            val response = api.updateOrderStatus(orderId, request.status)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error updating order status", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    // ==================== MENU MANAGEMENT ====================

    override suspend fun getMenuItems(restaurantId: String): Result<List<FoodMenuItem>> {
        return try {
            val response = api.getMenuItems(restaurantId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error fetching menu items", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun createMenuItem(
        restaurantId: String,
        request: MenuItemRequest
    ): Result<FoodMenuItem> {
        return try {
            // Validate request
            require(request.name.isNotBlank()) { "Menu item name is required" }
            require(request.price > 0) { "Price must be greater than 0" }
            require(request.category.isNotBlank()) { "Category is required" }
            
            val response = api.createMenuItem(restaurantId, request)
            Log.d("MerchantRepository", "Created menu item: ${response.name}")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error creating menu item", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun updateMenuItem(
        restaurantId: String,
        menuItemId: String,
        request: MenuItemRequest
    ): Result<FoodMenuItem> {
        return try {
            val response = api.updateMenuItem(restaurantId, menuItemId, request)
            Log.d("MerchantRepository", "Updated menu item: $menuItemId")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error updating menu item", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun deleteMenuItem(
        restaurantId: String,
        menuItemId: String
    ): Result<Unit> {
        return try {
            api.deleteMenuItem(restaurantId, menuItemId)
            Log.d("MerchantRepository", "Deleted menu item: $menuItemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error deleting menu item", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun toggleMenuItemAvailability(
        restaurantId: String,
        menuItemId: String
    ): Result<FoodMenuItem> {
        return try {
            val response = api.toggleMenuItemAvailability(restaurantId, menuItemId)
            Log.d("MerchantRepository", "Toggled menu item availability: $menuItemId")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Log.e("MerchantRepository", "Error toggling menu item availability", e)
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}

// ==================== EXTENSION FUNCTIONS ====================

// MerchantRestaurantResponse now has toDomain() method in the data class itself

private fun OrderResponse.toDomain() = OrderWithDetails(
    id = id,
    customerId = customerId,
    customerName = customerName,
    customerPhone = "0912456789",
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    items = items.map { OrderItem(it.menuItemName, it.quantity) },
    totalPrice = totalPrice,
    totalItems = totalItems,
    status = OrderStatus.valueOf(status),
    orderDate = orderDate,
    deliveryAddress = deliveryAddress,
    cancelReason = cancelReason
)

// FoodMenuItemResponse now has toDomain() method in the data class itself
