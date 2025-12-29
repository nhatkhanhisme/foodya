package com.example.foodya.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun RestaurantEditDialog(
    restaurantName: String,
    name: String,
    address: String,
    phoneNumber: String,
    email: String,
    description: String,
    cuisine: String,
    openingTime: String,
    closingTime: String,
    openingHours: String,
    minimumOrder: String,
    maxDeliveryDistance: String,
    isUpdating: Boolean,
    error: String?,
    onNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCuisineChange: (String) -> Unit,
    onOpeningTimeChange: (String) -> Unit,
    onClosingTimeChange: (String) -> Unit,
    onOpeningHoursChange: (String) -> Unit,
    onMinimumOrderChange: (String) -> Unit,
    onMaxDeliveryDistanceChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (!isUpdating) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit Restaurant",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = restaurantName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            enabled = !isUpdating
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Error message
                    if (error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Basic Information Section
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Tên nhà hàng *") },
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        singleLine = true,
                        supportingText = { Text("2-200 ký tự") }
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = onAddressChange,
                        label = { Text("Địa chỉ *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        minLines = 2,
                        maxLines = 3,
                        supportingText = { Text("Tối đa 500 ký tự") }
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        label = { Text("Số điện thoại *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        placeholder = { Text("+84909123456 hoặc 0909123456") },
                        supportingText = { Text("Định dạng: +[mã vùng][số]") }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        placeholder = { Text("restaurant@example.com") }
                    )

                    OutlinedTextField(
                        value = cuisine,
                        onValueChange = onCuisineChange,
                        label = { Text("Loại ẩm thực *") },
                        leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        singleLine = true,
                        placeholder = { Text("VD: Việt Nam, Ý, Nhật") },
                        supportingText = { Text("Tối đa 100 ký tự") }
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Mô tả") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        minLines = 3,
                        maxLines = 5,
                        supportingText = { Text("Tối đa 2000 ký tự") }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Operating Hours Section
                    Text(
                        text = "Giờ hoạt động",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = openingTime,
                            onValueChange = onOpeningTimeChange,
                            label = { Text("Giờ mở cửa") },
                            modifier = Modifier.weight(1f),
                            enabled = !isUpdating,
                            placeholder = { Text("08:00") },
                            singleLine = true,
                            supportingText = { Text("HH:mm") }
                        )

                        OutlinedTextField(
                            value = closingTime,
                            onValueChange = onClosingTimeChange,
                            label = { Text("Giờ đóng cửa") },
                            modifier = Modifier.weight(1f),
                            enabled = !isUpdating,
                            placeholder = { Text("22:00") },
                            singleLine = true,
                            supportingText = { Text("HH:mm") }
                        )
                    }

                    OutlinedTextField(
                        value = openingHours,
                        onValueChange = onOpeningHoursChange,
                        label = { Text("Mô tả giờ mở cửa") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        placeholder = { Text("T2-T6: 08:00-22:00") },
                        minLines = 2,
                        supportingText = { Text("Tối đa 200 ký tự") }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Delivery Information Section
                    Text(
                        text = "Thông tin giao hàng",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = minimumOrder,
                        onValueChange = onMinimumOrderChange,
                        label = { Text("Đơn tối thiểu (VND)") },
                        leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("50000") },
                        singleLine = true,
                        supportingText = { Text("Phải ≥ 0") }
                    )

                    OutlinedTextField(
                        value = maxDeliveryDistance,
                        onValueChange = onMaxDeliveryDistanceChange,
                        label = { Text("Khoảng cách tối đa (km)") },
                        leadingIcon = { Icon(Icons.Default.DeliveryDining, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text("10") },
                        singleLine = true,
                        supportingText = { Text("0-100 km") }
                    )
                }

                // Footer with action buttons
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isUpdating) "Đang lưu..." else "Lưu thay đổi")
                    }
                }
            }
        }
    }
}
