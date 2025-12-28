package com.example.foodya.ui.screen.customer.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.foodya.domain.model.Food
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailView(
    restaurantId: String,
    onBackClick: () -> Unit,
    onGoToCheckout: () -> Unit,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    // 1. Collect các State từ ViewModel
    val menuList by viewModel.menuList.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val cartMap by viewModel.cartMap.collectAsState() // Cần thêm dòng này trong ViewModel như Bước 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết nhà hàng $restaurantId") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            // 2. Chỉ hiện Box tổng tiền khi số lượng > 0
            if (cartSummary.totalQuantity > 0) {
                BottomCartSummaryBox(
                    itemCount = cartSummary.totalQuantity,
                    totalPrice = cartSummary.totalPrice,
                    onCheckoutClick = onGoToCheckout
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 3. Danh sách món ăn
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(menuList) { foodItem ->
                    // Lấy số lượng từ Map dựa trên ID của món ăn
                    val quantity = cartMap[foodItem.id]?.quantity ?: 0

                    FoodItemRow(
                        item = foodItem,
                        quantity = quantity,
                        onAdd = { viewModel.addItem(foodItem) },
                        onRemove = { viewModel.removeItem(foodItem) }
                    )
                }
            }
        }
    }
}

// Component hiển thị từng dòng món ăn
@Composable
fun FoodItemRow(
    item: Food,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh món ăn
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin món + Nút bấm
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatCurrency(item.price),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Logic nút bấm: 0 thì hiện (+), >0 hiện (- số lượng +)
                    if (quantity == 0) {
                        IconButton(
                            onClick = onAdd,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Giảm", tint = Color.Gray)
                            }
                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Tăng", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomCartSummaryBox(
    itemCount: Int,
    totalPrice: Double,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Đã chọn: $itemCount món", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Tổng: ${formatCurrency(totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Button(onClick = onCheckoutClick) {
                Text("Đặt món")
            }
        }
    }
}

// Hàm format tiền tệ (Ví dụ)
fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}