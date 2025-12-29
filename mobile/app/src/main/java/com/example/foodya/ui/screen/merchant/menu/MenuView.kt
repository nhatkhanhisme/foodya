package com.example.foodya.ui.screen.merchant.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.ui.screen.merchant.MerchantViewModel
import com.example.foodya.ui.screen.merchant.menu.components.MenuItemCard
import com.example.foodya.ui.screen.merchant.menu.components.MenuItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuView(
    viewModel: MerchantViewModel
) {
    val state by viewModel.menuState.collectAsState()

    // Edit/Create dialog
    if (state.showEditDialog) {
        MenuItemDialog(
            isCreating = state.isCreating,
            name = state.formName,
            description = state.formDescription,
            price = state.formPrice,
            imageUrl = state.formImageUrl,
            category = state.formCategory,
            isAvailable = state.formIsAvailable,
            isProcessing = state.isProcessing,
            selectedImageUri = state.selectedImageUri,
            isUploadingImage = state.isUploadingImage,
            onNameChange = viewModel::onFormNameChange,
            onDescriptionChange = viewModel::onFormDescriptionChange,
            onPriceChange = viewModel::onFormPriceChange,
            onImageUrlChange = viewModel::onFormImageUrlChange,
            onCategoryChange = viewModel::onFormCategoryChange,
            onIsAvailableChange = viewModel::onFormIsAvailableChange,
            onImageSelected = viewModel::onImageSelected,
            onImageCleared = viewModel::onImageCleared,
            onSave = viewModel::onSaveItem,
            onDismiss = viewModel::onDismissMenuDialog
        )
    }

    // Delete confirmation dialog
    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissMenuDialog,
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa món \"${state.itemToDelete?.name}\"?") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmDelete,
                    enabled = !state.isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Xóa")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::onDismissMenuDialog,
                    enabled = !state.isProcessing
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý thực đơn", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (state.selectedRestaurant != null) {
                FloatingActionButton(
                    onClick = viewModel::onAddNewItem,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm món mới"
                    )
                }
            }
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
                    EmptyRestaurantState()
                }
                else -> {
                    MenuContent(
                        state = state,

                        onEditItem = viewModel::onEditItem,
                        onDeleteItem = viewModel::onDeleteItem
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
                        TextButton(onClick = viewModel::onMenuRetry) {
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
private fun EmptyRestaurantState() {
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
            text = "Đăng ký nhà hàng để quản lý thực đơn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun MenuContent(
    state: MenuState,
    onEditItem: (FoodMenuItem) -> Unit,
    onDeleteItem: (FoodMenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Restaurant header (read-only)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nhà hàng",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.selectedRestaurant?.name ?: "Chưa chọn",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Menu items section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thực đơn (${state.menuItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.menuItems.isEmpty()) {
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
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có món ăn nào",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nhấn + để thêm món mới",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            items(state.menuItems.filter { it.isActive }) { item ->
                MenuItemCard(
                    item = item,
                    onEdit = { onEditItem(item) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }
    }
}