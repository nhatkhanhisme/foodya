package com.example.foodya.ui.screen.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager // Để xử lý logout
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Giả lập API call delay
            delay(1000)

            // Mock Data đúng theo cấu trúc JSON bạn yêu cầu
            val mockUser = User(
                id = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                username = "nguyenvana_foodya",
                email = "nguyenvana@email.com",
                fullName = "Nguyễn Văn A",
                phoneNumber = "+84 909 123 456",
                role = "CUSTOMER",
                isActive = true,
                isEmailVerified = true,
                lastLoginAt = "2025-12-25T07:49:56.843Z",
                createdAt = "2025-01-01T07:49:56.843Z",
                updatedAt = "2025-12-25T07:49:56.843Z"
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    user = mockUser
                )
            }
        }
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN ---

    fun onToggleDarkMode(isDark: Boolean) {
        // Trong thực tế, bạn sẽ lưu setting này vào DataStore để áp dụng toàn app
        _state.update { it.copy(isDarkMode = isDark) }
    }

    fun onLogout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // 1. Xóa token và role trong DataStore
            tokenManager.clear()
            delay(500) // Giả lập chút delay cho mượt

            // 2. Gọi callback để View điều hướng về màn Login
            onLogoutSuccess()
        }
    }
}