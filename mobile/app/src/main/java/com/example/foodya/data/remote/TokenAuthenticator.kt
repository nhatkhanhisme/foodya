package com.example.foodya.data.remote

import android.util.Log
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
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenRefresh", "Phát hiện lỗi 401. Đang thực hiện Refresh Token...")
        if (responseCount(response) >= 2) {
            return null // Trả về null để báo lỗi hẳn -> Logout
        }

        val refreshToken = tokenManager.getRefreshTokenBlocking() ?: return null

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
            Log.e("TokenRefresh", "Refresh Token thất bại. Cần đăng nhập lại.")
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