package com.example.foodya.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is HttpException -> parseHttpErrorBody(this)
        is IOException -> "Không thể kết nối mạng. Vui lòng kiểm tra internet."
        else -> this.message ?: "Đã xảy ra lỗi không xác định."
    }
}

private fun parseHttpErrorBody(e: HttpException): String {
    try {
        val errorBody = e.response()?.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            val jsonObject = JSONObject(errorBody)
            
            // Handle validation errors with field-specific messages
            if (jsonObject.has("validationErrors")) {
                val validationErrors = jsonObject.getJSONArray("validationErrors")
                if (validationErrors.length() > 0) {
                    val errorMessages = mutableListOf<String>()
                    for (i in 0 until validationErrors.length()) {
                        val error = validationErrors.getJSONObject(i)
                        val field = error.optString("field", "")
                        val message = error.optString("message", "")
                        errorMessages.add("• $field: $message")
                    }
                    return "Lỗi xác thực:\n" + errorMessages.joinToString("\n")
                }
            }
            
            // Handle standard error response
            if (jsonObject.has("message")) {
                return jsonObject.getString("message")
            }
            
            if (jsonObject.has("error")) {
                return jsonObject.getString("error")
            }
            
            return "Lỗi server: $errorBody"
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    // Fallback based on HTTP status code
    return when (e.code()) {
        400 -> "Yêu cầu không hợp lệ. Vui lòng kiểm tra thông tin nhập."
        401 -> "Tên đăng nhập hoặc mật khẩu không đúng."
        403 -> "Truy cập bị từ chối."
        404 -> "Không tìm thấy tài nguyên."
        409 -> "Dữ liệu đã tồn tại."
        500 -> "Lỗi máy chủ. Vui lòng thử lại sau."
        503 -> "Dịch vụ không khả dụng. Vui lòng thử lại sau."
        else -> "Lỗi kết nối (Mã: ${e.code()})"
    }
}