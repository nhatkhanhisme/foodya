package com.example.foodya.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is HttpException -> parseHttpErrorBody(this)
        is IOException -> "Network connection failed. Please check your internet."
        else -> this.message ?: "An unknown error occurred."
    }
}

private fun parseHttpErrorBody(e: HttpException): String {
    try {
        val errorBody = e.response()?.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            val jsonObject = JSONObject(errorBody)
            if (jsonObject.has("error") && jsonObject.has("message")) {
                val errType = jsonObject.getString("error")
                val errMsg = jsonObject.getString("message")
                return "$errType: $errMsg"
            }
            return when {
                jsonObject.has("message") -> jsonObject.getString("message")
                jsonObject.has("error") -> jsonObject.getString("error")
                else -> "Server error: $errorBody"
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    // Fallback (Nếu Server không trả về JSON hoặc không đọc được)
    return when (e.code()) {
        400 -> "Invalid request. Please check your inputs."
        401 -> "Invalid username or password."
        403 -> "Access denied (Forbidden)."
        404 -> "Resource not found."
        500 -> "Internal server error. Please try again later."
        503 -> "Service unavailable. Please try again later."
        else -> "Connection error (Code: ${e.code()})"
    }
}