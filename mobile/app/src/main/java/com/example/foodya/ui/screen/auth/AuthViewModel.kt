package com.example.foodya.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.UserRole
import com.example.foodya.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // 1. Dùng MutableStateFlow để quản lý trạng thái
    private val _state = MutableStateFlow(AuthState())

    // 2. Public ra ngoài dưới dạng StateFlow (chỉ đọc) để UI quan sát
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // --- Cập nhật field (Sử dụng hàm update để an toàn luồng) ---
    fun onUsernameChange(v: String) {
        _state.update { it.copy(username = v) }
    }

    fun onPasswordChange(v: String) {
        _state.update { it.copy(password = v) }
    }

    fun onEmailChange(v: String) {
        _state.update { it.copy(email = v) }
    }

    fun onFullNameChange(v: String) {
        _state.update { it.copy(fullName = v) }
    }

    fun onPhoneNumberChange(v: String) {
        _state.update { it.copy(phoneNumber = v) }
    }

    fun onRoleChange(v: UserRole) {
        _state.update { it.copy(role = v) }
    }

    fun toggleAuthMode() {
        _state.update {
            it.copy(isLoginMode = !it.isLoginMode, error = null)
        }
    }

    fun submit() {
        // Lấy giá trị hiện tại bằng .value
        if (_state.value.isLoginMode) login() else register()
    }

    // --- LOGIN ---
    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Lấy value hiện tại để gọi API
            val currentState = _state.value
            val result = repository.login(currentState.username, currentState.password)

            result.onSuccess { response ->
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            }.onFailure { e ->
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Login failed")
                }
            }
        }
    }

    // --- REGISTER ---
    private fun register() {
        val currentState = _state.value

        if (currentState.username.isBlank() || currentState.password.isBlank() || currentState.email.isBlank()) {
            _state.update { it.copy(error = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.register(
                username = currentState.username,
                password = currentState.password,
                email = currentState.email,
                fullName = currentState.fullName,
                phoneNumber = currentState.phoneNumber,
                role = currentState.role
            )

            result.onSuccess {
                // Đăng ký xong chuyển sang login mode
                _state.update { it.copy(isLoading = false, isLoginMode = true) }
            }.onFailure { e ->
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Register failed")
                }
            }
        }
    }
}