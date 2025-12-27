package com.example.foodya.ui.screen.customer.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.OrderItemSummary
import com.example.foodya.domain.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(OrderHistoryState())
    val state = _state.asStateFlow()

    // Lưu trữ toàn bộ đơn hàng (Master List)
    private var allOrders = listOf<Order>()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Giả lập delay mạng
            delay(1000)

            // Mock Data
            allOrders = mockOrderData()

            // Lọc dữ liệu ban đầu theo tab mặc định (PENDING)
            filterOrders(_state.value.selectedStatus)
        }
    }

    // Hàm chuyển Tab
    fun onStatusSelected(status: OrderStatus) {
        _state.update { it.copy(selectedStatus = status) }
        filterOrders(status)
    }

    // Logic lọc danh sách
    private fun filterOrders(status: OrderStatus) {
        val filtered = allOrders.filter { it.status == status }
        _state.update {
            it.copy(
                isLoading = false,
                orders = filtered
            )
        }
    }

    // --- MOCK DATA ---
    private fun mockOrderData(): List<Order> {
        val list = mutableListOf<Order>()

        // Tạo giả 10 đơn hàng với status ngẫu nhiên
        OrderStatus.values().forEachIndexed { index, status ->
            repeat(2) { i ->
                list.add(
                    Order(
                        id = "ORD-${status.name}-$i",
                        restaurantName = if(i % 2 == 0) "The Italian Corner" else "Pho Viet Nam",
                        restaurantImageUrl = "https://via.placeholder.com/150",
                        totalPrice = 15.5 + i * 2,
                        totalItems = 2 + i,
                        status = status,
                        orderDate = "25 Dec, 10:30 AM",
                        items = listOf(
                            OrderItemSummary("Pizza Margherita", 1),
                            OrderItemSummary("Coke Zero", 2)
                        )
                    )
                )
            }
        }
        return list
    }
}