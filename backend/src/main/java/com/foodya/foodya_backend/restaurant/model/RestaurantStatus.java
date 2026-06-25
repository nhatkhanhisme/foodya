package com.foodya.foodya_backend.restaurant.model;

public enum RestaurantStatus {
    PENDING,    // Mới tạo, chờ admin duyệt — chưa hiển thị với customer
    APPROVED,   // Đã duyệt — hiển thị và có thể đặt hàng
    REJECTED,   // Bị từ chối — owner nhận thông báo kèm lý do, có thể tái nộp
    SUSPENDED   // Admin tạm ngưng vì vi phạm chính sách — ẩn với customer
}
