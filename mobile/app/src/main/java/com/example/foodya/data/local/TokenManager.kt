package com.example.foodya.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodya.data.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val TOKEN_EXPIRES_AT = longPreferencesKey("token_expires_at")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val role: Flow<String?> = context.dataStore.data.map { it[USER_ROLE_KEY] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    private val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN] }

    /**
     * Save complete authentication response after login/register
     */
    suspend fun saveAuthResponse(
        accessToken: String,
        refreshToken: String,
        role: UserRole,
        userId: String,
        username: String,
        expiresIn: Long
    ) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[USER_ROLE_KEY] = role.name
            prefs[USER_ID_KEY] = userId
            prefs[USERNAME_KEY] = username
            prefs[TOKEN_EXPIRES_AT] = System.currentTimeMillis() + expiresIn
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    suspend fun saveTokens(access: String, refresh: String, role: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
            it[USER_ROLE_KEY] = role
        }
    }

    /**
     * Update tokens after refresh (preserves user info)
     */
    suspend fun updateTokens(access: String, refresh: String, expiresIn: Long? = null) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
            expiresIn?.let { expiry ->
                it[TOKEN_EXPIRES_AT] = System.currentTimeMillis() + expiry
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    fun getRefreshTokenBlocking(): String? {
        return runBlocking {
            refreshToken.firstOrNull()
        }
    }

    fun getRoleBlocking(): UserRole? {
        return runBlocking {
            role.firstOrNull()?.let { UserRole.valueOf(it) }
        }
    }

    fun getUserIdBlocking(): String? {
        return runBlocking {
            userId.firstOrNull()
        }
    }
}
