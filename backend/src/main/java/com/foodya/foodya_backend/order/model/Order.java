package com.foodya.foodya_backend.order.model;

import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.common.exception.AppException;
import com.foodya.foodya_backend.common.exception.ErrorCode;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "orderItems", "customer", "restaurant" })
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // ========== ORDER SUMMARY ==========

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Integer totalItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Instant orderDate;

    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    @Column(length = 500)
    private String cancelReason;

    @Column(nullable = false)
    @Builder.Default
    private Double deliveryFee = 0.0;

    @Column(length = 1000)
    private String orderNotes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // ========== HELPER METHODS ==========

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOW_TRANSITIONS = Map.of(
            OrderStatus.AWAITING_PAYMENT, Set.of(OrderStatus.PENDING, OrderStatus.CANCELLED),
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED),
            OrderStatus.READY_FOR_PICKUP, Set.of(OrderStatus.PICKED_UP, OrderStatus.CANCELLED),
            OrderStatus.PICKED_UP, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.REJECTED, Set.of(),
            OrderStatus.CANCELLED, Set.of());

    public UUID getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public UUID getRestaurantId() {
        return restaurant != null ? restaurant.getId() : null;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotals();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalPrice = orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum() + this.deliveryFee;

        this.totalItems = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public void updateStatus(OrderStatus newStatus) {
        Set<OrderStatus> allowed = ALLOW_TRANSITIONS.getOrDefault(this.status, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_TRANSITION,
                    "Cannot transition from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
    }

    public void cancel(String reason) {
       updateStatus(OrderStatus.CANCELLED);
        this.cancelReason = reason; 
    }

    public boolean isCancellable() {
        return this.status == OrderStatus.AWAITING_PAYMENT
                || this.status == OrderStatus.PENDING
                || this.status == OrderStatus.CONFIRMED;
    }

    public boolean isDelivered() {
        return this.status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        recalculateTotals();
    }

}
