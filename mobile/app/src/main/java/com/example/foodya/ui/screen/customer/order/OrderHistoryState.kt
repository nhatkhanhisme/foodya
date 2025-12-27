package com.example.foodya.ui.screen.customer.order

import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.OrderStatus

data class OrderHistoryState(
    val isLoading: Boolean = true,

    val selectedStatus: OrderStatus = OrderStatus.PENDING,

    val orders: List<Order> = emptyList()
)