package com.example.foodya.data.model

import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.OrderItem
import com.example.foodya.domain.model.enums.OrderStatus
import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("customerId")
    val customerId: String,
    
    @SerializedName("customerName")
    val customerName: String,
    
    @SerializedName("restaurantId")
    val restaurantId: String,
    
    @SerializedName("restaurantName")
    val restaurantName: String,
    
    @SerializedName("restaurantImageUrl")
    val restaurantImageUrl: String? = null,
    
    @SerializedName("restaurantPhone")
    val restaurantPhone: String? = null,
    
    @SerializedName("items")
    val items: List<OrderItemResponse> = emptyList(),
    
    @SerializedName("subtotal")
    val subtotal: Double = 0.0,
    
    @SerializedName("deliveryFee")
    val deliveryFee: Double = 0.0,
    
    @SerializedName("totalPrice")
    val totalPrice: Double = 0.0,
    
    @SerializedName("totalItems")
    val totalItems: Int = 0,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("deliveryAddress")
    val deliveryAddress: String,
    
    @SerializedName("orderNotes")
    val orderNotes: String? = null,
    
    @SerializedName("cancelReason")
    val cancelReason: String? = null,
    
    @SerializedName("orderDate")
    val orderDate: String,
    
    @SerializedName("orderDateFormatted")
    val orderDateFormatted: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String,
    
    @SerializedName("canCancel")
    val canCancel: Boolean = false
)

fun OrderResponse.toDomain(): Order {
    return Order(
        id = id,
        customerId = customerId,
        customerName = customerName,
        restaurantId = restaurantId,
        restaurantName = restaurantName,
        restaurantImageUrl = restaurantImageUrl,
        restaurantPhone = restaurantPhone,
        items = items.map { it.toDomain() },
        subtotal = subtotal,
        deliveryFee = deliveryFee,
        totalPrice = totalPrice,
        totalItems = totalItems,
        status = OrderStatus.valueOf(status),
        deliveryAddress = deliveryAddress,
        orderNotes = orderNotes,
        cancelReason = cancelReason,
        orderDate = orderDate,
        orderDateFormatted = orderDateFormatted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        canCancel = canCancel
    )
}

data class OrderItemResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("menuItemId")
    val menuItemId: String,
    
    @SerializedName("menuItemName")
    val menuItemName: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("priceAtPurchase")
    val priceAtPurchase: Double,
    
    @SerializedName("subtotal")
    val subtotal: Double,
    
    @SerializedName("specialInstructions")
    val specialInstructions: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
)

fun OrderItemResponse.toDomain(): OrderItem {
    return OrderItem(
        id = id,
        menuItemId = menuItemId,
        menuItemName = menuItemName,
        quantity = quantity,
        priceAtPurchase = priceAtPurchase,
        subtotal = subtotal,
        specialInstructions = specialInstructions
    )
}

data class UpdateOrderStatusRequest(
    val status: String,
    val cancelReason: String?
)
