package com.example.foodya.ui.screen.customer.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.foodya.ui.components.BottomCartSummaryBox
import com.example.foodya.ui.components.FoodItemRow
import com.example.foodya.util.toCurrency

/**
 * RestaurantDetailView - Main composable for Restaurant Detail screen
 * 
 * Features:
 * - State-based rendering (Loading/Success/Error)
 * - Parallax header with restaurant cover image
 * - Sticky category headers
 * - Cart management with floating bottom summary
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailView(
    restaurantId: String,
    onBackClick: () -> Unit,
    onGoToCheckout: () -> Unit,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when (val state = uiState) {
                            is RestaurantUiState.Success -> state.restaurant.name
                            else -> "Chi tiết nhà hàng"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Show cart summary only in Success state with items
            if (uiState is RestaurantUiState.Success) {
                val successState = uiState as RestaurantUiState.Success
                if (successState.cartSummary.totalQuantity > 0) {
                    BottomCartSummaryBox(
                        cartItems = successState.cartMap,
                        itemCount = successState.cartSummary.totalQuantity,
                        totalPrice = successState.cartSummary.totalPrice,
                        onCheckoutClick = onGoToCheckout
                    )
                }
            }
        }
    ) { paddingValues ->
        // State-based content rendering
        when (val state = uiState) {
            is RestaurantUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is RestaurantUiState.Error -> {
                ErrorState(
                    onRetry = viewModel::retry,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is RestaurantUiState.Success -> {
                SuccessContent(
                    state = state,
                    onAddItem = viewModel::addItem,
                    onRemoveItem = viewModel::removeItem,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Loading State - Shows centered progress indicator
 */
@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Đang tải thực đơn...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error State - Shows friendly error message with retry button
 */
@Composable
private fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            // Title
            Text(
                text = "Nhà hàng này đóng cửa mất rồi",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Description
            Text(
                text = "Hãy thử những món mới nhé!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Retry Button
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Thử lại")
            }
        }
    }
}

/**
 * Success Content - Main content with parallax header and menu list
 */
@Composable
private fun SuccessContent(
    state: RestaurantUiState.Success,
    onAddItem: (com.example.foodya.domain.model.Food) -> Unit,
    onRemoveItem: (com.example.foodya.domain.model.Food) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Parallax Header with Restaurant Cover Image
        item {
            RestaurantHeader(restaurant = state.restaurant)
        }

        // Restaurant Info Card
        item {
            RestaurantInfoCard(restaurant = state.restaurant)
        }

        // Menu Items grouped by category with Sticky Headers
        state.groupedMenu.forEach { (category, items) ->
            // Sticky Category Header
            stickyHeader {
                CategoryHeader(category = category)
            }

            // Food Items in this category
            items(items, key = { it.id }) { foodItem ->
                val quantity = state.cartMap[foodItem.id]?.quantity ?: 0

                FoodItemRow(
                    item = foodItem,
                    quantity = quantity,
                    onAdd = { onAddItem(foodItem) },
                    onRemove = { onRemoveItem(foodItem) }
                )
            }
        }

        // Empty state if no menu items
        if (state.groupedMenu.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có món ăn nào",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Restaurant Header - Parallax cover image with gradient overlay
 */
@Composable
private fun RestaurantHeader(
    restaurant: com.example.foodya.domain.model.Restaurant
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Cover Image
        AsyncImage(
            model = restaurant.imageUrl,
            contentDescription = "Restaurant cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Restaurant Name
        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

/**
 * Restaurant Info Card - Shows rating, delivery time, and address
 */
@Composable
private fun RestaurantInfoCard(
    restaurant: com.example.foodya.domain.model.Restaurant
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rating & Delivery Info
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Text(
                    text = "⭐ ${restaurant.rating}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                // Delivery Time
                Text(
                    text = "🕐 ${restaurant.estimatedDeliveryTime} phút",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Delivery Fee
                Text(
                    text = if (restaurant.deliveryFee > 0) {
                        "💵 ${restaurant.deliveryFee.toCurrency()}"
                    } else {
                        "🎉 Miễn phí ship"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (restaurant.deliveryFee == 0.0) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (restaurant.deliveryFee == 0.0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Description
            if (restaurant.description.isNotBlank()) {
                Text(
                    text = restaurant.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alpha(0.8f)
                )
            }

            // Address
            Text(
                text = "📍 ${restaurant.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Category Header - Sticky header for menu sections
 */
@Composable
private fun CategoryHeader(
    category: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}