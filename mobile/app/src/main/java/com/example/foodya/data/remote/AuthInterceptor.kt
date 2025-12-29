package com.example.foodya.data.remote

import com.example.foodya.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Không thêm token cho các endpoint auth
        if (originalRequest.url.encodedPath.contains("/auth/refresh") ||
            originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/register")) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking {
            tokenManager.accessToken.firstOrNull()
        }

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}