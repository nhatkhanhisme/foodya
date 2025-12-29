package com.example.foodya.ui.screen.customer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.ui.components.OrderDetailDialog
import com.example.foodya.ui.components.OrderItemCard
import com.example.foodya.ui.screen.customer.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryView(
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val state by viewModel.orderHistoryState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf<String?>(null) }
    var cancelReason by remember { mutableStateOf("") }
    var showOrderDetail by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    // Show cancel error snackbar
    LaunchedEffect(state.cancelError) {
        if (state.cancelError != null) {
            // Error will be shown in snackbar, clear after showing
        }
    }

    val tabs = listOf(
        OrderStatus.PENDING,
        OrderStatus.PREPARING,
        OrderStatus.SHIPPING,
        OrderStatus.DELIVERED,
        OrderStatus.CANCELLED
    )

    Scaffold(
        snackbarHost = {
            if (state.cancelError != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearCancelError() }) {
                            Text("Đóng")
                        }
                    }
                ) {
                    Text(state.cancelError ?: "")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Text(
                text = "Đơn hàng của tôi",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(state.selectedStatus).takeIf { it >= 0 } ?: 0,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[tabs.indexOf(state.selectedStatus).takeIf { it >= 0 } ?: 0]
                        ),
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
                                    fontWeight = if (state.selectedStatus == status) 
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // Content with Pull to Refresh
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.refreshOrders() },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    state.error != null && state.orders.isEmpty() -> {
                        // Error State
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.error ?: "Đã xảy ra lỗi",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refreshOrders() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thử lại")
                            }
                        }
                    }
                    state.orders.isEmpty() -> {
                        // Empty State
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có đơn hàng nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        // Order List
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.orders, key = { it.id }) { order ->
                                OrderItemCard(
                                    order = order,
                                    onClick = { 
                                        selectedOrder = order
                                        showOrderDetail = true
                                    },
                                    onCancel = if (order.canCancel) {
                                        {
                                            selectedOrderId = order.id
                                            showCancelDialog = true
                                        }
                                    } else null,
                                    isCancelling = state.isCancelling && state.cancellingOrderId == order.id
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancel Dialog
    if (showCancelDialog && selectedOrderId != null) {
        AlertDialog(
            onDismissRequest = { 
                showCancelDialog = false
                cancelReason = ""
            },
            title = { Text("Hủy đơn hàng") },
            text = {
                Column {
                    Text("Bạn có chắc muốn hủy đơn hàng này?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Lý do hủy (không bắt buộc)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedOrderId?.let { orderId ->
                            viewModel.cancelOrder(
                                orderId, 
                                cancelReason.ifBlank { null }
                            )
                        }
                        showCancelDialog = false
                        cancelReason = ""
                        selectedOrderId = null
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCancelDialog = false
                    cancelReason = ""
                }) {
                    Text("Đóng")
                }
            }
        )
    }
    
    // Order Detail Dialog
    if (showOrderDetail && selectedOrder != null) {
        OrderDetailDialog(
            order = selectedOrder!!,
            onDismiss = {
                showOrderDetail = false
                selectedOrder = null
            }
        )
    }
}