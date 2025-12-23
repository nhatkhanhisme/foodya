package com.example.foodya.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    tokenManager: TokenManager
) : ViewModel() {

    // Biến này lưu Role trong RAM. Mất khi tắt App.
    val userRole = tokenManager.role.map { roleString ->
        if (roleString != null) {
            try {
                UserRole.valueOf(roleString) // Convert String -> Enum
            } catch (e: Exception) {
                UserRole.CUSTOMER // Fallback nếu lỗi
            }
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}