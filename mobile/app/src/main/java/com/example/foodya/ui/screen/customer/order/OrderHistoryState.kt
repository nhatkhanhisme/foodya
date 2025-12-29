package com.example.foodya.ui.screen.customer.order

import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.enums.OrderStatus

data class OrderHistoryState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedStatus: OrderStatus = OrderStatus.PENDING,
    val orders: List<Order> = emptyList(),
    val allOrders: List<Order> = emptyList(),
    val isCancelling: Boolean = false,
    val cancelError: String? = null,
    val cancellingOrderId: String? = null
)