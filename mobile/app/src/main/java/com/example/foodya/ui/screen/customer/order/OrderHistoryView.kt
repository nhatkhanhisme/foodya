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
import com.example.foodya.domain.model.OrderStatus

@Composable
fun OrderHistoryView(
    viewModel: OrderHistoryViewModel = hiltViewModel(),
    onOrderClick: (String) -> Unit // Callback xem chi tiết đơn hàng
) {
    val state by viewModel.state.collectAsState()

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

// --- COMPONENT: ORDER CARD ---
@Composable
fun OrderItemCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Card: Tên quán + Trạng thái
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    AsyncImage(
                        model = order.restaurantImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.restaurantName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = order.orderDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                // Badge Status
                Surface(
                    color = order.status.getColor().copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = order.status.label,
                        color = order.status.getColor(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // Body Card: Tóm tắt món ăn
            order.items.take(2).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.foodName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (order.items.size > 2) {
                Text("... và ${order.items.size - 2} món khác", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // Footer Card: Tổng tiền + Nút hành động
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tổng cộng", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = "$${order.totalPrice}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Nút hành động tùy theo trạng thái
                if (order.status == OrderStatus.DELIVERED) {
                    Button(
                        onClick = { /* Logic Đặt lại */ },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Đặt lại")
                    }
                } else {
                    OutlinedButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Chi tiết")
                    }
                }
            }
        }
    }
}