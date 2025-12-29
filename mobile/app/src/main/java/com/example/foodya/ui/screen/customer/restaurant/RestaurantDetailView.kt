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
import com.example.foodya.ui.components.BottomCartSummaryBox
import com.example.foodya.ui.components.FoodItemRow
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

// Hàm format tiền tệ (Ví dụ)
fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}