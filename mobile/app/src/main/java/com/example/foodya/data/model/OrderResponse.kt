package com.example.foodya.data.model

data class OrderResponse(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<OrderItemResponse>,
    val totalPrice: Double,
    val totalItems: Int,
    val status: String,
    val orderDate: String,
    val deliveryAddress: String,
    val cancelReason: String?
)

data class OrderItemResponse(
    val name: String,
    val quantity: Int
)

data class UpdateOrderStatusRequest(
    val status: String,
    val cancelReason: String?
)
