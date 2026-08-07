package com.foodya.foodya_backend.ordering.domain;

import com.foodya.foodya_backend.catalog.domain.MenuItem;
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
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"order", "menuItem"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @JsonIgnore
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    // BR-18: item name captured at order time so later menu edits never alter history
    // V8 migration added this column and back-filled from menu_items.name
    @Column(name = "item_name_snapshot", nullable = false, length = 500)
    private String itemNameSnapshot;

    // BR-18: price captured at order time (V8 migration renamed column price_at_purchase → item_price_snapshot)
    // Field name kept as priceAtPurchase so existing callers (OrderService, OrderItemResponse) compile unchanged.
    @Column(name = "item_price_snapshot", nullable = false)
    private Long priceAtPurchase;

    @Column(nullable = false)
    private Long subtotal;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    private void calculateSubtotal() {
        this.subtotal = this.quantity * this.priceAtPurchase;
    }

    // menuItem itself is @JsonIgnore'd, so responses need these flattened out
    public UUID getMenuItemId() {
        return menuItem != null ? menuItem.getId() : null;
    }

    public String getMenuItemName() {
        return menuItem != null ? menuItem.getName() : null;
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        calculateSubtotal();
    }
}
