package com.example.foodya.domain.model

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    val priceAtPurchase: Double,
    val subtotal: Double,
    val specialInstructions: String?
)