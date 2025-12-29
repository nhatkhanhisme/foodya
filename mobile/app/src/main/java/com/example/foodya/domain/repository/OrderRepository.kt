package com.example.foodya.domain.repository

import com.example.foodya.data.model.OrderRequest
import com.example.foodya.data.model.OrderResponse

interface OrderRepository {
    suspend fun createOrder(request: OrderRequest): Result<OrderResponse>
    suspend fun getMyOrders(): Result<List<OrderResponse>>
    suspend fun getMyActiveOrders(): Result<List<OrderResponse>>
    suspend fun cancelOrder(orderId: String, reason: String?): Result<OrderResponse>
}
