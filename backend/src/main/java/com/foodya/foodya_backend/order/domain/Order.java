package com.foodya.foodya_backend.order.domain;

import com.foodya.foodya_backend.user.domain.User;
import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.domain.Restaurant;
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

import java.math.BigDecimal;
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

    // Shipper assigned at UC-S03; null until then (BR-13: version guards concurrent assignment)
    @Column(name = "shipper_id")
    private UUID shipperId;

    // Snapshot of delivery address string, kept for legacy rows pre-dating V7 migration
    // V7 migration renamed column delivery_address → delivery_address_snapshot
    @Column(name = "delivery_address_snapshot", length = 500)
    private String deliveryAddress;

    // FK to addresses table added in V7 migration (nullable for pre-migration rows)
    @Column(name = "delivery_address_id")
    private UUID deliveryAddressId;

    // V7 migration renamed column total_price → total
    @Column(name = "total", nullable = false)
    private Long totalPrice;

    // V7 migration renamed column delivery_fee → shipping_fee (BR-07)
    @Column(name = "shipping_fee", nullable = false)
    @Builder.Default
    private Long deliveryFee = 0L;

    // Item-only sum before shipping fee (SRS §6.1); back-filled by V7 migration
    @Column(name = "subtotal", nullable = false)
    @Builder.Default
    private Long subtotal = 0L;

    @Column(nullable = false)
    private Integer totalItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Instant orderDate;

    @Column(length = 500)
    private String cancelReason;

    @Column(length = 1000)
    private String orderNotes;

    // BR-19: road distance fetched once at checkout and stored; source flags FALLBACK if provider failed
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "distance_source", length = 10)
    private String distanceSource;

    // BR-13: optimistic lock prevents concurrent shipper assignment racing.
    // MUST stay null until persisted: Spring Data decides persist-vs-merge for
    // @Version entities by "version == null" — a default of 0 sends new orders
    // down the merge path, whose half-populated copy zeroes the totals in
    // @PrePersist. Hibernate initializes it to 0 on insert.
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // ========== STATUS TIMESTAMPS ==========

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "picked_up_at")
    private Instant pickedUpAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

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

    void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotals();
    }

    void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.subtotal = orderItems.stream()
                .mapToLong(OrderItem::getSubtotal)
                .sum();
        this.totalPrice = this.subtotal + (this.deliveryFee != null ? this.deliveryFee : 0L);

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
        stampTransitionTime(newStatus);
    }

    // Stamped here, not in services, so every caller (merchant, admin, future
    // shipper flow) records lifecycle times consistently. Popularity buckets
    // depend on deliveredAt reflecting the real delivery moment.
    private void stampTransitionTime(OrderStatus newStatus) {
        Instant now = Instant.now();
        switch (newStatus) {
            case CONFIRMED -> this.confirmedAt = now;
            case PICKED_UP -> this.pickedUpAt = now;
            case DELIVERED -> this.deliveredAt = now;
            case CANCELLED -> this.cancelledAt = now;
            default -> { }
        }
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

    boolean isDelivered() {
        return this.status == OrderStatus.DELIVERED;
    }

    boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        recalculateTotals();
    }

}
