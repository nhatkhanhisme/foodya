package com.example.foodya.domain.model

import com.example.foodya.domain.model.enums.OrderStatus

data class Order(
    val id: String,
    val customerId: String,
    val customerName: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String?,
    val restaurantPhone: String?,
    val items: List<OrderItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val totalPrice: Double,
    val totalItems: Int,
    val status: OrderStatus,
    val deliveryAddress: String,
    val orderNotes: String?,
    val cancelReason: String?,
    val orderDate: String,
    val orderDateFormatted: String?,
    val createdAt: String,
    val updatedAt: String,
    val canCancel: Boolean
)