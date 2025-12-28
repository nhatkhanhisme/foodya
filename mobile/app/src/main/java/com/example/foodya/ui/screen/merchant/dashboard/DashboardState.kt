package com.example.foodya.ui.screen.merchant.dashboard

import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.model.OrderWithDetails

data class DashboardState(
    val isLoading: Boolean = true,
    val myRestaurants: List<MerchantRestaurant> = emptyList(),
    val selectedRestaurant: MerchantRestaurant? = null,
    val orders: List<OrderWithDetails> = emptyList(),
    val pendingOrdersCount: Int = 0,
    val selectedOrder: OrderWithDetails? = null,
    val isUpdatingOrderStatus: Boolean = false,
    val error: String? = null,
    val cancelReason: String = "",
    val showCancelReasonDialog: Boolean = false
)
