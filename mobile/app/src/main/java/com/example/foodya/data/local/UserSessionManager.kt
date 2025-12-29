package com.example.foodya.data.local

import com.example.foodya.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserSessionManager provides easy access to current user session information.
 * Acts as a facade over TokenManager with convenient session-related methods.
 */
@Singleton
class UserSessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    
    /**
     * Get current user role as Flow
     */
    val currentRole: Flow<UserRole?> = tokenManager.role.combine(tokenManager.accessToken) { role, token ->
        if (token != null && role != null) {
            try {
                UserRole.valueOf(role)
            } catch (e: IllegalArgumentException) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Check if user is authenticated
     */
    val isAuthenticated: Flow<Boolean> = tokenManager.accessToken.combine(tokenManager.role) { token, role ->
        !token.isNullOrBlank() && !role.isNullOrBlank()
    }

    /**
     * Get user ID
     */
    val userId: Flow<String?> = tokenManager.userId

    /**
     * Get username
     */
    val username: Flow<String?> = tokenManager.username

    /**
     * Check if current user is a customer
     */
    suspend fun isCustomer(): Boolean {
        return currentRole.firstOrNull() == UserRole.CUSTOMER
    }

    /**
     * Check if current user is a merchant
     */
    suspend fun isMerchant(): Boolean {
        return currentRole.firstOrNull() == UserRole.MERCHANT
    }

    /**
     * Check if current user is an admin
     */
    suspend fun isAdmin(): Boolean {
        return currentRole.firstOrNull() == UserRole.ADMIN
    }

    /**
     * Get current role synchronously (blocks current thread)
     * Use this only when necessary, prefer Flow-based approach
     */
    fun getCurrentRoleBlocking(): UserRole? {
        return tokenManager.getRoleBlocking()
    }

    /**
     * Get current user ID synchronously (blocks current thread)
     */
    fun getUserIdBlocking(): String? {
        return tokenManager.getUserIdBlocking()
    }

    /**
     * Clear all session data (logout)
     */
    suspend fun clearSession() {
        tokenManager.clear()
    }
}
