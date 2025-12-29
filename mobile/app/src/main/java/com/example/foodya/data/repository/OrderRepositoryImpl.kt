package com.example.foodya.data.repository

import android.util.Log
import com.example.foodya.data.model.OrderRequest
import com.example.foodya.data.model.OrderResponse
import com.example.foodya.data.remote.OrderApi
import com.example.foodya.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository {

    override suspend fun createOrder(request: OrderRequest): Result<OrderResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = orderApi.createOrder(request)
                Log.d("OrderRepository", "Order created successfully: ${response.id}")
                Result.success(response)
            } catch (e: Exception) {
                Log.e("OrderRepository", "Failed to create order", e)
                Result.failure(Exception(e.message ?: "Failed to create order"))
            }
        }

    override suspend fun getMyOrders(): Result<List<OrderResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val orders = orderApi.getMyOrders()
                Log.d("OrderRepository", "Fetched ${orders.size} orders")
                Result.success(orders)
            } catch (e: Exception) {
                Log.e("OrderRepository", "Failed to fetch orders", e)
                Result.failure(Exception(e.message ?: "Failed to fetch orders"))
            }
        }

    override suspend fun getMyActiveOrders(): Result<List<OrderResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val orders = orderApi.getMyActiveOrders()
                Log.d("OrderRepository", "Fetched ${orders.size} active orders")
                Result.success(orders)
            } catch (e: Exception) {
                Log.e("OrderRepository", "Failed to fetch active orders", e)
                Result.failure(Exception(e.message ?: "Failed to fetch active orders"))
            }
        }

    override suspend fun cancelOrder(orderId: String, reason: String?): Result<OrderResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = orderApi.cancelOrder(orderId, reason)
                Log.d("OrderRepository", "Order cancelled: $orderId")
                Result.success(response)
            } catch (e: Exception) {
                Log.e("OrderRepository", "Failed to cancel order", e)
                Result.failure(Exception(e.message ?: "Failed to cancel order"))
            }
        }
}
