package com.example.foodya.ui.screen.merchant.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodya.domain.model.OrderWithDetails
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.ui.components.RestaurantEditDialog
import com.example.foodya.ui.screen.merchant.MerchantViewModel
import com.example.foodya.util.toCurrency
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(
    viewModel: MerchantViewModel,
    onNavigateToRegisterRestaurant: () -> Unit
) {
    val state by viewModel.dashboardState.collectAsState()

    // Restaurant edit dialog
    if (state.showEditRestaurantDialog && state.selectedRestaurant != null) {
        RestaurantEditDialog(
            restaurantName = state.selectedRestaurant!!.name,
            name = state.editName,
            address = state.editAddress,
            phoneNumber = state.editPhoneNumber,
            email = state.editEmail,
            description = state.editDescription,
            cuisine = state.editCuisine,
            openingTime = state.editOpeningTime,
            closingTime = state.editClosingTime,
            openingHours = state.editOpeningHours,
            minimumOrder = state.editMinimumOrder,
            maxDeliveryDistance = state.editMaxDeliveryDistance,
            isUpdating = state.isUpdatingRestaurant,
            error = state.editRestaurantError,
            onNameChange = viewModel::onEditNameChange,
            onAddressChange = viewModel::onEditAddressChange,
            onPhoneNumberChange = viewModel::onEditPhoneNumberChange,
            onEmailChange = viewModel::onEditEmailChange,
            onDescriptionChange = viewModel::onEditDescriptionChange,
            onCuisineChange = viewModel::onEditCuisineChange,
            onOpeningTimeChange = viewModel::onEditOpeningTimeChange,
            onClosingTimeChange = viewModel::onEditClosingTimeChange,
            onOpeningHoursChange = viewModel::onEditOpeningHoursChange,
            onMinimumOrderChange = viewModel::onEditMinimumOrderChange,
            onMaxDeliveryDistanceChange = viewModel::onEditMaxDeliveryDistanceChange,
            onSave = viewModel::saveRestaurantChanges,
            onDismiss = viewModel::hideEditRestaurantDialog
        )
    }

    // Order status dialog
    if (state.selectedOrder != null) {
        OrderStatusDialog(
            order = state.selectedOrder!!,
            isUpdating = state.isUpdatingOrderStatus,
            showCancelReasonDialog = state.showCancelReasonDialog,
            cancelReason = state.cancelReason,
            onCancelReasonChange = viewModel::onCancelReasonChange,
            onUpdateStatus = viewModel::onUpdateOrderStatus,
            onConfirmCancellation = viewModel::onConfirmCancellation,
            onDismiss = viewModel::onDismissOrderDialog
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FOODYA",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.dp.value.sp
                        )
                        Text(
                            text = "Quản lý nhà hàng",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        BadgedBox(
                            badge = {
                                if (state.pendingOrdersCount > 0) {
                                    Badge {
                                        Text("${state.pendingOrdersCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Thông báo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.myRestaurants.isEmpty() -> {
                    EmptyRestaurantState(onNavigateToRegister = onNavigateToRegisterRestaurant)
                }
                else -> {
                    DashboardContent(
                        state = state,
                        onRestaurantSelected = viewModel::onRestaurantSelected,
                        onOrderClick = viewModel::onOrderClick,
                        onEditRestaurant = viewModel::showEditRestaurantDialog,
                        onToggleStatus = viewModel::toggleRestaurantStatus
                    )
                }
            }

            // Error snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::onDashboardRetry) {
                            Text("Thử lại")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun EmptyRestaurantState(onNavigateToRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Store,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có nhà hàng",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Đăng ký nhà hàng để bắt đầu nhận đơn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng ký nhà hàng")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardState,
    onRestaurantSelected: (com.example.foodya.domain.model.MerchantRestaurant) -> Unit,
    onOrderClick: (OrderWithDetails) -> Unit,
    onEditRestaurant: () -> Unit,
    onToggleStatus: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Restaurant selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nhà hàng hiện tại",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (state.myRestaurants.size > 1) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = state.selectedRestaurant?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                state.myRestaurants.forEach { restaurant ->
                                    DropdownMenuItem(
                                        text = { Text(restaurant.name) },
                                        onClick = {
                                            onRestaurantSelected(restaurant)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = state.selectedRestaurant?.name ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Restaurant action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEditRestaurant,
                            modifier = Modifier.weight(1f),
                            enabled = !state.isTogglingStatus
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chỉnh sửa")
                        }
                        
                        Button(
                            onClick = onToggleStatus,
                            modifier = Modifier.weight(1f),
                            enabled = !state.isTogglingStatus,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.selectedRestaurant?.isOpen == true) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (state.isTogglingStatus) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = if (state.selectedRestaurant?.isOpen == true) 
                                        Icons.Default.Close 
                                    else 
                                        Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (state.selectedRestaurant?.isOpen == true) 
                                    "Đóng cửa" 
                                else 
                                    "Mở cửa"
                            )
                        }
                    }
                }
            }
        }

        // Summary card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Đơn hàng cần xử lý",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.pendingOrdersCount}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // Orders section
        item {
            Text(
                text = "Danh sách đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có đơn hàng nào",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(state.orders) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderClick(order) }
                )
            }
        }
    }
}
@Composable
private fun OrderCard(
    order: OrderWithDetails,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Order ID và Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "#${order.id.takeLast(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thông tin khách hàng
            InfoRow(
                icon = Icons.Filled.Person,
                text = order.customerName
            )

            InfoRow(
                icon = Icons.Filled.Phone,
                text = order.customerPhone
            )

            InfoRow(
                icon = Icons.Filled.LocationOn,
                text = order.deliveryAddress,
                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Danh sách món
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = order.items.joinToString(", ") { "${it.name} x${it.quantity}" },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Ngày và tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = order.orderDate.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.totalPrice.toCurrency(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val backgroundColor = status.getColor().copy(alpha = 0.2f)
    val textColor = status.getColor()
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OrderStatusDialog(
    order: OrderWithDetails,
    isUpdating: Boolean,
    showCancelReasonDialog: Boolean,
    cancelReason: String,
    onCancelReasonChange: (String) -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onConfirmCancellation: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showCancelReasonDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Lý do hủy đơn") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = onCancelReasonChange,
                    label = { Text("Nhập lý do hủy") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmCancellation,
                    enabled = !isUpdating && cancelReason.isNotBlank()
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Xác nhận")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss, enabled = !isUpdating) {
                    Text("Hủy")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Cập nhật trạng thái đơn hàng") },
            text = {
                Column {
                    Text(
                        text = "Đơn hàng #${order.id.takeLast(8)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trạng thái hiện tại: ${order.status.label}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn trạng thái mới:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Available status transitions
                    val availableStatuses = when (order.status) {
                        OrderStatus.PENDING -> listOf(
                            OrderStatus.PREPARING,
                            OrderStatus.CANCELLED
                        )
                        OrderStatus.PREPARING -> listOf(
                            OrderStatus.SHIPPING
                        )
                        OrderStatus.SHIPPING -> listOf(
                            OrderStatus.DELIVERED
                        )
                        else -> emptyList()
                    }
                    
                    availableStatuses.forEach { status ->
                        StatusButton(
                            status = status,
                            isEnabled = !isUpdating,
                            onClick = { onUpdateStatus(status) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss, enabled = !isUpdating) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
private fun StatusButton(
    status: OrderStatus,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = status.getColor(),
            contentColor = Color.White
        ),
        enabled = isEnabled
    ) {
        Text(status.label)
    }
}