package com.example.foodya.ui.screen.merchant.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.foodya.domain.model.User
import com.example.foodya.ui.screen.merchant.MerchantViewModel
import com.example.foodya.ui.components.RestaurantEditDialog

@Composable
fun MerchantProfileView(
    viewModel: MerchantViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    val dashboardState by viewModel.dashboardState.collectAsState()

    // Create restaurant dialog
    if (dashboardState.showCreateRestaurantDialog) {
        RestaurantEditDialog(
            restaurantName = "Tạo nhà hàng mới",
            name = dashboardState.createName,
            address = dashboardState.createAddress,
            phoneNumber = dashboardState.createPhoneNumber,
            email = dashboardState.createEmail,
            description = dashboardState.createDescription,
            cuisine = dashboardState.createCuisine,
            openingTime = dashboardState.createOpeningTime,
            closingTime = dashboardState.createClosingTime,
            openingHours = dashboardState.createOpeningHours,
            minimumOrder = dashboardState.createMinimumOrder,
            maxDeliveryDistance = dashboardState.createMaxDeliveryDistance,
            isUpdating = dashboardState.isCreatingRestaurant,
            error = dashboardState.createRestaurantError,
            onNameChange = viewModel::onCreateNameChange,
            onAddressChange = viewModel::onCreateAddressChange,
            onPhoneNumberChange = viewModel::onCreatePhoneNumberChange,
            onEmailChange = viewModel::onCreateEmailChange,
            onDescriptionChange = viewModel::onCreateDescriptionChange,
            onCuisineChange = viewModel::onCreateCuisineChange,
            onOpeningTimeChange = viewModel::onCreateOpeningTimeChange,
            onClosingTimeChange = viewModel::onCreateClosingTimeChange,
            onOpeningHoursChange = viewModel::onCreateOpeningHoursChange,
            onMinimumOrderChange = viewModel::onCreateMinimumOrderChange,
            onMaxDeliveryDistanceChange = viewModel::onCreateMaxDeliveryDistanceChange,
            onSave = viewModel::createNewRestaurant,
            onDismiss = viewModel::hideCreateRestaurantDialog
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (state.isLoading && state.user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                MerchantProfileHeader(user = state.user!!)

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                Text(
                    text = "Quản lý",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Add restaurant
                ProfileMenuItem(
                    icon = Icons.Outlined.AddBusiness,
                    title = "Thêm nhà hàng mới",
                    onClick = viewModel::showCreateRestaurantDialog
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                Text(
                    text = "Cài đặt chung",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Dark mode toggle
                ProfileMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Chế độ tối",
                    trailingContent = {
                        Switch(
                            checked = state.isDarkMode,
                            onCheckedChange = viewModel::onMerchantToggleDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )

                // Change password
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Đổi mật khẩu",
                    onClick = onNavigateToChangePassword
                )

                // Terms & Policies
                ProfileMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "Chính sách & Điều khoản",
                    onClick = onNavigateToTerms
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = { viewModel.onMerchantLogout(onLogoutSuccess = onNavigateToLogin) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }

                // Version info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Phiên bản 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun MerchantProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    ),
                    startY = 0f,
                    endY = 600f
                )
            )
            .padding(top = 40.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with border
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=${user.fullName}&background=random&size=200",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Verified badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Đã xác thực",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name with role badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Merchant",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contact info cards
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Phone
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = user.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = trailingContent ?: {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
    )
    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        thickness = 0.5.dp
    )
}