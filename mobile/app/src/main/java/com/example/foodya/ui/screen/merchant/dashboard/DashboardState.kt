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
    val showCancelReasonDialog: Boolean = false,
    
    // Restaurant Edit Dialog State
    val showEditRestaurantDialog: Boolean = false,
    val isUpdatingRestaurant: Boolean = false,
    val editRestaurantError: String? = null,
    val editName: String = "",
    val editAddress: String = "",
    val editPhoneNumber: String = "",
    val editEmail: String = "",
    val editDescription: String = "",
    val editCuisine: String = "",
    val editOpeningTime: String = "",
    val editClosingTime: String = "",
    val editOpeningHours: String = "",
    val editMinimumOrder: String = "",
    val editMaxDeliveryDistance: String = "",
    val isTogglingStatus: Boolean = false
)
