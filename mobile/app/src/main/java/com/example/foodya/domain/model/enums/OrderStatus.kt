package com.example.foodya.domain.model.enums

import androidx.compose.ui.graphics.Color

enum class OrderStatus(val label: String, val colorHex: Long) {
    PENDING("Chờ xác nhận", 0xFFFFA726),    // Cam nhạt
    PREPARING("Đang chế biến", 0xFF29B6F6), // Xanh dương
    SHIPPING("Đang giao", 0xFF7E57C2),      // Tím
    DELIVERED("Đã giao", 0xFF66BB6A),       // Xanh lá
    CANCELLED("Đã hủy", 0xFFEF5350);        // Đỏ

    // Helper để lấy Color trong Compose
    fun getColor(): Color = Color(colorHex)
}