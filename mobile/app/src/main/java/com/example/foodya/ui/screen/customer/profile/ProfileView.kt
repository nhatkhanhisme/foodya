package com.example.foodya.ui.screen.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
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
import com.example.foodya.ui.screen.customer.CustomerViewModel

@Composable
fun CustomerProfileView(
    viewModel: CustomerViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit, // Callback khi logout thành công
    onNavigateToChangePassword: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()

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
                // --- PHẦN 1: HEADER USER INFO ---
                ProfileHeader(user = state.user!!)

                Spacer(modifier = Modifier.height(24.dp))

                // --- PHẦN 2: SETTINGS ---
                Text(
                    text = "Cài đặt chung",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // 1. Chế độ tối (Switch)
                ProfileMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Chế độ tối",
                    trailingContent = {
                        Switch(
                            checked = state.isDarkMode,
                            onCheckedChange = viewModel::onToggleDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )

                // 2. Đổi mật khẩu
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Đổi mật khẩu",
                    onClick = onNavigateToChangePassword
                )

                // 3. Chính sách & Điều khoản
                ProfileMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "Chính sách & Điều khoản",
                    onClick = onNavigateToTerms
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- PHẦN 3: ĐĂNG XUẤT ---
                Button(
                    onClick = { viewModel.onLogout(onLogoutSuccess = onNavigateToLogin) },
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

                // Hiển thị version app
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Phiên bản 1.0.0", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar giả lập
        AsyncImage(
            model = "https://ui-avatars.com/api/?name=${user.fullName}&background=random&size=200",
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tên hiển thị
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        // Email/SĐT
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = user.phoneNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Badge Role (Khách hàng)
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape
        ) {
            Text(
                text = "Thành viên",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
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
            // Mặc định hiện mũi tên nếu không có content khác
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
    )
    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
}