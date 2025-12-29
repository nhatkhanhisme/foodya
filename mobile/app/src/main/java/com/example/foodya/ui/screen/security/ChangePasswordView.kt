package com.example.foodya.ui.screen.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordView(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Handle success state
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.onDismissSuccess()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đổi mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            // Show error snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Để bảo mật tài khoản, vui lòng nhập mật khẩu hiện tại và chọn mật khẩu mới có ít nhất 6 ký tự.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current Password Field
            OutlinedTextField(
                value = state.currentPassword,
                onValueChange = viewModel::onCurrentPasswordChange,
                label = { Text("Mật khẩu hiện tại *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.isPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (state.isPasswordVisible) 
                                "Ẩn mật khẩu" 
                            else 
                                "Hiện mật khẩu"
                        )
                    }
                },
                singleLine = true,
                enabled = !state.isLoading
            )

            // New Password Field
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                label = { Text("Mật khẩu mới *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                supportingText = {
                    Text("Tối thiểu 6 ký tự")
                },
                singleLine = true,
                enabled = !state.isLoading
            )

            // Confirm Password Field
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Xác nhận mật khẩu mới *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !state.isLoading,
                isError = state.confirmPassword.isNotBlank() && 
                         state.newPassword != state.confirmPassword,
                supportingText = {
                    if (state.confirmPassword.isNotBlank() && 
                        state.newPassword != state.confirmPassword) {
                        Text(
                            text = "Mật khẩu xác nhận không khớp",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Update Button
            Button(
                onClick = viewModel::onChangePassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading && viewModel.isFormValid(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Cập nhật mật khẩu", fontWeight = FontWeight.Bold)
                }
            }

            // Additional info
            Text(
                text = "Lưu ý: Sau khi đổi mật khẩu thành công, bạn sẽ quay lại trang trước.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
