package com.example.foodya.ui.screen.merchant.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.model.OrderWithDetails
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.domain.repository.MerchantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val merchantRepo: MerchantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = merchantRepo.getMyRestaurants()
            result.onSuccess { restaurants ->
                Log.d("DashboardViewModel", "Loaded ${restaurants.size} restaurants")
                if (restaurants.isNotEmpty()) {
                    val firstRestaurant = restaurants.first()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = restaurants,
                            selectedRestaurant = firstRestaurant,
                        )
                    }
                    loadOrders(firstRestaurant.id)
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = emptyList()
                        )
                    }
                }
            }.onFailure { error ->
                Log.e("DashboardViewModel", "Error loading restaurants: ${error.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

    private fun loadOrders(restaurantId: String) {
        viewModelScope.launch {
            val result = merchantRepo.getOrdersByRestaurant(restaurantId)
            result.onSuccess { orders ->
                Log.d("DashboardViewModel", "Loaded ${orders.size} orders")
                val pendingCount = orders.count { it.status == OrderStatus.PENDING }
                _state.update {
                    it.copy(
                        orders = orders,
                        pendingOrdersCount = pendingCount
                    )
                }
            }.onFailure { error ->
                Log.e("DashboardViewModel", "Error loading orders: ${error.message}")
                _state.update {
                    it.copy(error = error.message)
                }
            }
        }
    }

    fun onRestaurantSelected(restaurant: MerchantRestaurant) {
        _state.update { it.copy(selectedRestaurant = restaurant, orders = emptyList()) }
        loadOrders(restaurant.id)
    }

    fun onOrderClick(order: OrderWithDetails) {
        _state.update { it.copy(selectedOrder = order) }
    }

    fun onDismissOrderDialog() {
        _state.update { it.copy(selectedOrder = null, cancelReason = "", showCancelReasonDialog = false) }
    }

    fun onCancelReasonChange(reason: String) {
        _state.update { it.copy(cancelReason = reason) }
    }

    fun onUpdateOrderStatus(newStatus: OrderStatus) {
        val currentOrder = _state.value.selectedOrder ?: return
        
        // If status is CANCELLED, show cancel reason dialog
        if (newStatus == OrderStatus.CANCELLED) {
            _state.update { it.copy(showCancelReasonDialog = true) }
            return
        }
        
        updateOrderStatusInternal(currentOrder.id, newStatus, null)
    }

    fun onConfirmCancellation() {
        val currentOrder = _state.value.selectedOrder ?: return
        val reason = _state.value.cancelReason
        
        if (reason.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập lý do hủy") }
            return
        }
        
        updateOrderStatusInternal(currentOrder.id, OrderStatus.CANCELLED, reason)
    }

    private fun updateOrderStatusInternal(orderId: String, status: OrderStatus, cancelReason: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingOrderStatus = true, error = null) }
            delay(500) // Simulate network delay
            
            val result = merchantRepo.updateOrderStatus(orderId, status, cancelReason)
            result.onSuccess { updatedOrder ->
                Log.d("DashboardViewModel", "Order status updated: ${updatedOrder.status}")
                // Refresh orders list
                _state.value.selectedRestaurant?.let { restaurant ->
                    loadOrders(restaurant.id)
                }
                _state.update {
                    it.copy(
                        isUpdatingOrderStatus = false,
                        selectedOrder = null,
                        cancelReason = "",
                        showCancelReasonDialog = false
                    )
                }
            }.onFailure { error ->
                Log.e("DashboardViewModel", "Error updating order status: ${error.message}")
                _state.update {
                    it.copy(
                        isUpdatingOrderStatus = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onNavigateToRegisterRestaurant() {
        // This will be handled by the UI navigation
        Log.d("DashboardViewModel", "Navigate to register restaurant")
    }

    fun onRetry() {
        loadDashboard()
    }
}
