package com.example.foodya.ui.screen.customer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.ui.components.OrderItemCard
import com.example.foodya.ui.screen.customer.CustomerViewModel

@Composable
fun OrderHistoryView(
    viewModel: CustomerViewModel = hiltViewModel(),
    onOrderClick: (String) -> Unit // Callback xem chi tiết đơn hàng
) {
    val state by viewModel.orderHistoryState.collectAsState()

    // Danh sách các Status cần hiển thị trên Tab
    // Loại bỏ CANCELLED nếu bạn muốn giấu nó vào chỗ khác, hoặc giữ nguyên
    val tabs = listOf(
        OrderStatus.PENDING,
        OrderStatus.PREPARING,
        OrderStatus.SHIPPING,
        OrderStatus.DELIVERED
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. HEADER TITLE ---
        Text(
            text = "Đơn hàng của tôi",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )

        // --- 2. TAB ROW (Trạng thái) ---
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(state.selectedStatus).takeIf { it >= 0 } ?: 0,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(state.selectedStatus).takeIf { it >= 0 } ?: 0]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEach { status ->
                Tab(
                    selected = state.selectedStatus == status,
                    onClick = { viewModel.onStatusSelected(status) },
                    text = {
                        Text(
                            text = status.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (state.selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }

        // --- 3. LIST CONTENT ---
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.orders.isEmpty()) {
            // Empty State
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có đơn hàng nào", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.orders) { order ->
                    OrderItemCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}