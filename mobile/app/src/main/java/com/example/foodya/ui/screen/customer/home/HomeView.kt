package com.example.foodya.ui.screen.customer.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodya.R
import com.example.foodya.ui.components.CheckoutDialog
import com.example.foodya.ui.components.FoodDetailPopup
import com.example.foodya.ui.components.FoodItemCard
import com.example.foodya.ui.components.RestaurantCard
import com.example.foodya.ui.components.SectionTitle
import com.example.foodya.ui.screen.customer.CustomerViewModel
import com.example.foodya.ui.common.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: CustomerViewModel = hiltViewModel(),
    onNavigateToSearchResult: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onQuickOrderClick: (String) -> Unit
) {
    val state by viewModel.homeState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = if (event.isError) SnackbarDuration.Long else SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {
                    // Handle navigation if needed
                }
            }
        }
    }

    // Show checkout dialog
    if (state.showCheckoutDialog) {
        CheckoutDialog(
            cartItems = state.cartItems,
            restaurantName = state.selectedRestaurantName ?: "Unknown Restaurant",
            deliveryAddress = state.deliveryAddress,
            onAddressChange = viewModel::onDeliveryAddressChange,
            orderNotes = state.orderNotes,
            onNotesChange = viewModel::onOrderNotesChange,
            deliveryFee = state.deliveryFee,
            isLoading = state.isPlacingOrder,
            error = state.orderError,
            onConfirm = viewModel::placeOrder,
            onDismiss = viewModel::hideCheckout
        )
    }
    
    // Show success snackbar
    if (state.orderSuccess) {
        LaunchedEffect(Unit) {
            // Success is already handled in ViewModel with auto-close
        }
    }

    if (state.selectedFood != null) {
        FoodDetailPopup(
            food = state.selectedFood!!,
            onDismiss = viewModel::onDismissFoodDetail
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            // Header: Tên App
            if (!state.isSearchActive) {
                Image(
                    painter = painterResource(id = R.drawable.img_logo_full),
                    contentDescription = "Foodya Logo",
                    modifier = Modifier
                        .height(70.dp)
                        .wrapContentWidth()
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- THANH TÌM KIẾM ---
            val colors1 = SearchBarDefaults.colors()
            // Nội dung bên trong khi thanh search mở ra (Gợi ý)
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = {
                            viewModel.onSearchActiveChange(false)
                            onNavigateToSearchResult(it)
                        },
                        expanded = state.isSearchActive,
                        onExpandedChange = viewModel::onSearchActiveChange,
                        placeholder = { Text("Tìm nhà hàng...") },
                        leadingIcon = {
                            if (state.isSearchActive) {
                                // Hiển thị nút back khi search active
                                IconButton(onClick = {
                                    viewModel.onSearchActiveChange(false)
                                    viewModel.onClearSearch()
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                                }
                            } else {
                                // Hiển thị icon search khi không active
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            // Hiển thị nút X khi có text trong search query
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = viewModel::onClearSearch) {
                                    Icon(Icons.Default.Close, contentDescription = "Xóa")
                                }
                            }
                        },
                        colors = colors1.inputFieldColors,
                    )
                },
                expanded = state.isSearchActive,
                onExpandedChange = viewModel::onSearchActiveChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = colors1,
                content = {
                    // Nội dung bên trong khi thanh search mở ra (Gợi ý)
                    LazyColumn {
                        items(state.searchSuggestions) { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion) },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable {
                                    viewModel.onQueryChange(suggestion)
                                    viewModel.onSearchActiveChange(false)
                                    onNavigateToSearchResult(suggestion)
                                }
                            )
                        }
                    }
                },
            )

            // --- NỘI DUNG CHÍNH ---
            if (!state.isSearchActive) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for cart button
                        ) {
                            // Section: Nhà hàng gần đây
                            item {
                                SectionTitle("Gần bạn nhất")
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.nearbyRestaurants) { restaurant ->
                                        RestaurantCard(restaurant, onClick = { onRestaurantClick(restaurant.id) })
                                    }
                                }
                            }

                            // Section: Món ăn nổi bật
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                SectionTitle("Món ngon đang nổi")
                            }

                            items(state.popularFoods) { food ->
                                FoodItemCard(
                                    food = food,
                                    onDetailClick = {
                                        viewModel.onFoodSelected(food)
                                    },
                                    onOrderClick = {
                                        // Quick order - directly show checkout dialog
                                        viewModel.quickOrder(food, food.restaurantId, food.restaurantName)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
