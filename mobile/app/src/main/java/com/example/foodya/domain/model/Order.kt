package com.example.foodya.domain.model

import com.example.foodya.domain.model.enums.OrderStatus

data class Order(
    val id: String,
    val restaurantName: String,
    val restaurantImageUrl: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val totalItems: Int,
    val status: OrderStatus,
    val orderDate: String // ISO String: "2025-12-25T..."
)