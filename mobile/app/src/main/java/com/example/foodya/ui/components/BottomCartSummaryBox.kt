package com.example.foodya.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.foodya.domain.model.CartItem
import com.example.foodya.util.toCurrency

/**
 * BottomCartSummaryBox - Enhanced cart summary with detailed item list
 * 
 * Features:
 * - Shows detailed breakdown: "Item A x2, Item B x1..."
 * - Displays total quantity and price
 * - Close button to clear entire cart
 * - Confirmation dialog before clearing
 * - Responsive layout
 * - Integrates with RestaurantUiState
 */
@Composable
fun BottomCartSummaryBox(
    cartItems: Map<String, CartItem>,
    itemCount: Int,
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    onClearCart: () -> Unit = {}
) {
    var showClearDialog by remember { mutableStateOf(false) }

    // Confirmation dialog
    if (showClearDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Xóa giỏ hàng?") },
            text = { Text("Bạn có chắc muốn xóa tất cả $itemCount món đã chọn?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearCart()
                        showClearDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showClearDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = { showClearDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Xóa giỏ hàng",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            // Left side: Cart details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Item count
                Text(
                    text = "🛒 $itemCount món",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                // Detailed items breakdown
                val itemsText = buildCartItemsText(cartItems)
                Text(
                    text = itemsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Total price
                Text(
                    text = totalPrice.toCurrency(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Right side: Checkout button
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Đặt món")
            }
        }
    }
}

/**
 * Overload for backward compatibility
 */
@Composable
fun BottomCartSummaryBox(
    itemCount: Int,
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    onClearCart: () -> Unit = {}
) {
    BottomCartSummaryBox(
        cartItems = emptyMap(),
        itemCount = itemCount,
        totalPrice = totalPrice,
        onCheckoutClick = onCheckoutClick,
        onClearCart = onClearCart
    )
}

/**
 * Build detailed cart items text like "Pizza x2, Pasta x1, Salad x1"
 */
private fun buildCartItemsText(cartItems: Map<String, CartItem>): String {
    if (cartItems.isEmpty()) return "Giỏ hàng trống"
    
    return cartItems.values
        .sortedByDescending { it.quantity }
        .joinToString(", ") { item ->
            "${item.menuItem.name} x${item.quantity}"
        }
}