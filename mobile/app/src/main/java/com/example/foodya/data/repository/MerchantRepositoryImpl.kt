package com.example.foodya.data.repository

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
    
    override suspend fun getMyRestaurants(): Result<List<MerchantRestaurant>> {
        return try {
            val response = api.getMyRestaurants()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun registerRestaurant(
        name: String,
        address: String,
        description: String,
        deliveryFee: Double,
        estimatedDeliveryTime: Int,
        imageUrl: String
    ): Result<MerchantRestaurant> {
        return try {
            val request = RegisterRestaurantRequest(
                name = name,
                address = address,
                description = description,
                deliveryFee = deliveryFee,
                estimatedDeliveryTime = estimatedDeliveryTime,
                imageUrl = imageUrl
            )
            val response = api.registerRestaurant(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getOrdersByRestaurant(restaurantId: String): Result<List<OrderWithDetails>> {
        return try {
            val response = api.getOrdersByRestaurant(restaurantId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
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
            val response = api.updateOrderStatus(orderId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMenuItems(restaurantId: String): Result<List<FoodMenuItem>> {
        return try {
            val response = api.getMenuItems(restaurantId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun createMenuItem(
        restaurantId: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        category: String
    ): Result<FoodMenuItem> {
        return try {
            val request = CreateMenuItemRequest(
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl,
                category = category
            )
            val response = api.createMenuItem(restaurantId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun updateMenuItem(
        menuItemId: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        category: String,
        isAvailable: Boolean
    ): Result<FoodMenuItem> {
        return try {
            val request = UpdateMenuItemRequest(
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl,
                category = category,
                isAvailable = isAvailable
            )
            val response = api.updateMenuItem(menuItemId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun deleteMenuItem(menuItemId: String): Result<Unit> {
        return try {
            api.deleteMenuItem(menuItemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}

// Extension functions to convert DTOs to domain models
private fun MerchantRestaurantResponse.toDomain() = MerchantRestaurant(
    id = id,
    name = name,
    address = address,
    description = description,
    rating = rating,
    deliveryFee = deliveryFee,
    estimatedDeliveryTime = estimatedDeliveryTime,
    imageUrl = imageUrl,
    ownerId = ownerId,
    isActive = isActive
)

private fun OrderResponse.toDomain() = OrderWithDetails(
    id = id,
    customerId = customerId,
    customerName = customerName,
    customerPhone = customerPhone,
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    items = items.map { OrderItem(it.name, it.quantity) },
    totalPrice = totalPrice,
    totalItems = totalItems,
    status = OrderStatus.valueOf(status),
    orderDate = orderDate,
    deliveryAddress = deliveryAddress,
    cancelReason = cancelReason
)

private fun FoodMenuItemResponse.toDomain() = FoodMenuItem(
    id = id,
    restaurantId = restaurantId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    category = category,
    isAvailable = isAvailable,
    isActive = isActive
)
