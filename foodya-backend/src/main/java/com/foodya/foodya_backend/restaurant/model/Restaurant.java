package com.foodya.foodya_backend. restaurant.model;

import java.time.LocalDateTime;
import java. util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate. annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    // ========== BASIC INFORMATION ==========

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;  // ← THÊM MỚI

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String cuisine; // Vietnamese, Italian, Japanese, Korean, Thai, American, Chinese, Cafe

    // ========== MEDIA ==========

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String coverImageUrl;  // ← THÊM MỚI - Ảnh bìa lớn

    // ========== RATING & REVIEWS ==========

    @Column(nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalReviews = 0;

    // ========== STATUS ==========

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOpen = true;  // Đang mở cửa hay đóng cửa

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // Admin có thể deactivate

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerified = false;  // ← THÊM MỚI - Đã xác minh hay chưa

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;  // ← THÊM MỚI - Nổi bật trên trang chủ

    // ========== OPERATING HOURS ==========

    @Column(length = 10)
    private String openingTime; // Format: "HH:mm" - e.g., "09:00"

    @Column(length = 10)
    private String closingTime; // Format: "HH: mm" - e.g., "22:00"

    @Column(length = 200)
    private String openingHours;  // ← THÊM MỚI - "Mon-Fri:  09:00-22:00, Sat-Sun: 08:00-23:00"

    // ========== DELIVERY INFORMATION ==========

    @Column(nullable = false)
    @Builder.Default
    private Double deliveryFee = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double minimumOrder = 0.0;  // ← THÊM MỚI - Đơn tối thiểu

    @Column(nullable = false)
    @Builder.Default
    private Double freeDeliveryThreshold = 0.0;  // ← THÊM MỚI - Miễn phí ship từ giá trị đơn hàng

    private Integer estimatedDeliveryTime; // in minutes

    @Column(nullable = false)
    @Builder.Default
    private Double maxDeliveryDistance = 10.0;  // ← THÊM MỚI - Bán kính giao hàng (km)

    // ========== LOCATION (for future geo-search) ==========

    private Double latitude;   // ← THÊM MỚI

    private Double longitude;  // ← THÊM MỚI

    // ========== STATISTICS ==========

    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;  // ← THÊM MỚI - Tổng số đơn hàng

    @Column(nullable = false)
    @Builder.Default
    private Integer orderCount = 0;  // ← THÊM MỚI - Số đơn (dùng cho sort popular)

    @Column(nullable = false)
    @Builder.Default
    private Double averageOrderValue = 0.0;  // ← THÊM MỚI - Giá trị đơn trung bình

    // ========== PROMO & FEATURES ==========

    @Column(length = 200)
    private String promotionText;  // ← THÊM MỚI - "Giảm 20% cho đơn đầu tiên"

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasPromotion = false;  // ← THÊM MỚI - Đang có khuyến mãi

    @Column(nullable = false)
    @Builder.Default
    private Boolean acceptsCash = true;  // ← THÊM MỚI - Nhận COD

    @Column(nullable = false)
    @Builder.Default
    private Boolean acceptsCard = true;  // ← THÊM MỚI - Nhận thẻ/online payment

    // ========== TIMESTAMPS ==========

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;  // ← THÊM MỚI - Soft delete

    // ========== RELATIONSHIPS ==========

    // Relationship with MenuItem
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItems = new ArrayList<>();

    // Relationship with User (owner)
    @Column(columnDefinition = "UUID", nullable = false)
    private UUID ownerId;

    // Future relationships (comment for now)
    // @OneToMany(mappedBy = "restaurant")
    // private List<Order> orders;

    // @OneToMany(mappedBy = "restaurant")
    // private List<Review> reviews;

    // ========== HELPER METHODS ==========

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        menuItem.setRestaurant(this);
    }

    public void removeMenuItem(MenuItem menuItem) {
        menuItems. remove(menuItem);
        menuItem.setRestaurant(null);
    }

    public void updateRating(Double newRating, Integer reviewCount) {
        this.rating = newRating;
        this.totalReviews = reviewCount;
    }

    public void incrementOrderCount() {
        this.totalOrders++;
        this.orderCount++;
    }

    public void updateAverageOrderValue(Double totalRevenue) {
        if (this.totalOrders > 0) {
            this.averageOrderValue = totalRevenue / this. totalOrders;
        }
    }

    /**
     * Check if restaurant is currently open based on current time
     */
    public boolean isCurrentlyOpen() {
        if (! this.isOpen || ! this.isActive) {
            return false;
        }

        // TODO: Implement time-based check with openingTime/closingTime
        // For now, just return isOpen status
        return this.isOpen;
    }

    /**
     * Check if delivery is free for given order value
     */
    public boolean isFreeDelivery(Double orderValue) {
        return orderValue >= this.freeDeliveryThreshold;
    }

    /**
     * Calculate delivery fee for given order value
     */
    public Double calculateDeliveryFee(Double orderValue) {
        if (isFreeDelivery(orderValue)) {
            return 0.0;
        }
        return this.deliveryFee;
    }
}
