package com.example.foodya.data.model

data class OrderResponse(
    val id: String,
    val customerId: String,
    val customerName: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String? = null,
    val restaurantPhone: String? = null,
    val items: List<OrderItemResponse> = emptyList(), // bạn có thể định nghĩa class OrderItem riêng bên dưới
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0,
    val status: String,
    val deliveryAddress: String,
    val orderNotes: String? = null,
    val cancelReason: String? = null,
    val orderDate: String,             // ISO 8601 (ví dụ: "2025-12-29T11:31:50Z")
    val orderDateFormatted: String? = null, // ví dụ: "29/12/2025, 11:31 AM"
    val createdAt: String,
    val updatedAt: String,
    val canCancel: Boolean = false
)

data class OrderItemResponse(
    val id: String,
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    val priceAtPurchase: Double,
    val subtotal: Double,
    val specialInstructions: String? = null,
    val createdAt: String,
    val updatedAt: String
)


data class UpdateOrderStatusRequest(
    val status: String,
    val cancelReason: String?
)
