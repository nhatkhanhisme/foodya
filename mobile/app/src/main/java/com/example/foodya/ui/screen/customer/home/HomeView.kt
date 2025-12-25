package com.example.foodya.ui.screen.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodya.data.model.Food
import com.example.foodya.data.model.Restaurant
import coil.compose.AsyncImage
import com.example.foodya.ui.components.FoodDetailPopup
import com.example.foodya.ui.components.FoodItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearchResult: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onQuickOrderClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.selectedFood != null) {
        FoodDetailPopup(
            food = state.selectedFood!!,
            onDismiss = viewModel::onDismissFoodDetail
        )
    }

    Scaffold(
        topBar = {
            // Header: Tên App
            if (!state.isSearchActive) {
                Text(
                    text = "Foodya",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
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
                        placeholder = { Text("Tìm món ăn, nhà hàng...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = viewModel::onClearSearch) {
                                    Icon(Icons.Default.Close, contentDescription = null)
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
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
                                    onQuickOrderClick(food.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp, top = 8.dp)
    )
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(12.dp)) {
                Text(text = restaurant.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(text = "${restaurant.rating}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "${restaurant.estimatedDeliveryTime} mins", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}