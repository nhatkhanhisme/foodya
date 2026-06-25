package com. foodya.foodya_backend. order.model;

import com. foodya.foodya_backend. user.model.User;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"orderItems", "customer", "restaurant"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== RELATIONSHIPS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private User customer;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    /**
     * 🔥 ONE-TO-MANY: Order có nhiều OrderItem
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // ========== ORDER SUMMARY ==========

    /**
     * Tổng giá (tính từ tất cả orderItems)
     */
    @Column(nullable = false)
    private Double totalPrice;

    /**
     * Tổng số món (tính từ sum của quantity)
     */
    @Column(nullable = false)
    private Integer totalItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    @Column(length = 500)
    private String cancelReason;

    /**
     * Phí giao hàng (optional)
     */
    @Column(nullable = false)
    @Builder.Default
    private Double deliveryFee = 0.0;

    /**
     * Ghi chú chung cho đơn hàng
     */
    @Column(length = 1000)
    private String orderNotes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    public UUID getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public UUID getRestaurantId() {
        return restaurant != null ?  restaurant.getId() : null;
    }

    /**
     * Thêm OrderItem vào Order
     */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotals();
    }

    /**
     * Xóa OrderItem khỏi Order
     */
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotals();
    }

    /**
     * 🔥 QUAN TRỌNG: Tính lại tổng tiền và số món
     */
    public void recalculateTotals() {
        this.totalPrice = orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum() + this.deliveryFee;

        this.totalItems = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancelReason = reason;
    }

    public boolean isCancellable() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.PREPARING;
    }

    public boolean isDelivered() {
        return this.status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return this. status == OrderStatus.CANCELLED;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        recalculateTotals();
    }
}
