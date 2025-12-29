package com.example.foodya.data.remote

import android.util.Log
import com.example.foodya.data.local.AuthEventManager
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi>,
    private val authEventManager: AuthEventManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Check for both 401 (Unauthorized) and 403 (Forbidden)
        if (response.code != 401 && response.code != 403) {
            return null
        }

        // Không xử lý refresh token cho các request auth (login/register)
        val requestPath = response.request.url.encodedPath
        if (requestPath.contains("/auth/login") || 
            requestPath.contains("/auth/register") ||
            requestPath.contains("/auth/refresh")) {
            Log.d("TokenRefresh", "Bỏ qua refresh cho endpoint auth: $requestPath")
            return null
        }

        Log.d("TokenRefresh", "Phát hiện lỗi ${response.code}. Đang thực hiện Refresh Token...")
        
        if (responseCount(response) >= 2) {
            Log.e("TokenRefresh", "Refresh Token thất bại (quá số lần thử). Logout.")
            runBlocking { authEventManager.emitLogout() }
            return null
        }

        val refreshToken = tokenManager.getRefreshTokenBlocking()
        if (refreshToken == null) {
            Log.e("TokenRefresh", "Không tìm thấy Refresh Token. Logout.")
            runBlocking { authEventManager.emitLogout() }
            return null
        }

        return try {
            val authApi = authApiProvider.get()

            val refreshResponse = runBlocking {
                authApi.refresh(
                    RefreshRequest(refreshToken)
                )
            }
            
            runBlocking {
                tokenManager.updateTokens(
                    access = refreshResponse.accessToken,
                    refresh = refreshResponse.refreshToken
                )
            }

            Log.d("TokenRefresh", "Refresh Token thành công!")
            response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                .build()
        } catch (e: Exception) {
            Log.e("TokenRefresh", "Refresh Token thất bại. Cần đăng nhập lại.", e)
            runBlocking { authEventManager.emitLogout() }
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}