package com.example.foodya.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.UserRole
import com.example.foodya.ui.navigation.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow(Graph.AUTH)
    val startDestination = _startDestination.asStateFlow()

    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    val currentUserRole = _currentUserRole.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            // Sử dụng hàm combine để lắng nghe cả 2 luồng dữ liệu
            combine(
                tokenManager.accessToken,
                tokenManager.role
            ) { token: String?, roleString: String? ->
                val hasToken = !token.isNullOrBlank()
                val hasRole = !roleString.isNullOrBlank()

                if (hasToken && hasRole) {
                    try {
                        val roleEnum = UserRole.valueOf(roleString!!)
                        _currentUserRole.value = roleEnum
                        if (roleEnum == UserRole.MERCHANT) Graph.MERCHANT else Graph.CUSTOMER
                    } catch (e: Exception) {
                        Graph.AUTH
                    }
                } else {
                    Graph.AUTH
                }
            }.collect { destination ->
                _startDestination.value = destination
                _isLoading.value = false
            }
        }
    }
}