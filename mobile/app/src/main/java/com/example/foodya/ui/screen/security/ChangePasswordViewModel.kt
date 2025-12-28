package com.example.foodya.ui.screen.security

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.usecase.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state = _state.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _state.update { it.copy(currentPassword = value, error = null) }
    }

    fun onNewPasswordChange(value: String) {
        _state.update { it.copy(newPassword = value, error = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _state.update { it.copy(confirmPassword = value, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onChangePassword() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(500) // Simulate network delay

            val result = changePasswordUseCase(
                currentPassword = currentState.currentPassword,
                newPassword = currentState.newPassword,
                confirmPassword = currentState.confirmPassword
            )

            result.onSuccess {
                Log.d("ChangePasswordViewModel", "Password changed successfully")
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.e("ChangePasswordViewModel", "Error changing password: ${error.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onDismissSuccess() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun isFormValid(): Boolean {
        val state = _state.value
        return state.currentPassword.isNotBlank() &&
                state.newPassword.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                state.newPassword.length >= 6
    }
}
