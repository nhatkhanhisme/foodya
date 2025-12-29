package com.example.foodya.domain.model

import com.example.foodya.domain.model.enums.OrderStatus

data class OrderWithDetails(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val totalItems: Int,
    val status: OrderStatus,
    val orderDate: String,
    val deliveryAddress: String,
    val cancelReason: String? = null
)
