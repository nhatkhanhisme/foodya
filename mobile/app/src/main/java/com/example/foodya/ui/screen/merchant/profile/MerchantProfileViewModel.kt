package com.example.foodya.ui.screen.merchant.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.domain.model.User
import com.example.foodya.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MerchantProfileState())
    val state = _state.asStateFlow()

    init {
        loadMerchantProfile()
        observeThemePreference()
    }

    private fun observeThemePreference() {
        viewModelScope.launch {
            themeRepository.isDarkMode.collect { isDark ->
                _state.update { it.copy(isDarkMode = isDark ?: false) }
            }
        }
    }

    private fun loadMerchantProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Simulate API call delay
            delay(1000)

            // Mock merchant user data
            val mockUser = User(
                id = "merchant-001",
                username = "merchant_foodya",
                email = "merchant@foodya.com",
                fullName = "Quản lý nhà hàng",
                phoneNumber = "+84 901 234 567",
                role = "MERCHANT",
                isActive = true,
                isEmailVerified = true,
                lastLoginAt = "2025-12-28T07:49:56.843Z",
                createdAt = "2025-01-01T07:49:56.843Z",
                updatedAt = "2025-12-28T07:49:56.843Z"
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    user = mockUser
                )
            }
        }
    }

    fun onToggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }

    fun onLogout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Clear tokens and user data
            tokenManager.clear()
            delay(500)

            // Navigate to login
            onLogoutSuccess()
        }
    }
}
