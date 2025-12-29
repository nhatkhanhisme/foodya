package com.example.foodya.data.model

data class OrderRequest(
    val customerId: String? = null,  // Backend will use authenticated user
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    val deliveryAddress: String,
    val deliveryFee: Double = 0.0,
    val orderNotes: String? = null,
    val orderDate: String? = null
)

data class OrderItemRequest(
    val menuItemId: String,
    val quantity: Int,
    val notes: String? = null
)
