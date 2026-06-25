package com.foodya.foodya_backend.order.model;

import com.foodya.foodya_backend.restaurant.model.MenuItem;
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

    // ========== RELATIONSHIPS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @JsonIgnore
    private MenuItem menuItem;

    // ========== ORDER ITEM DETAILS ==========

    @Column(nullable = false)
    private Integer quantity;

    /**
     * 🔥 QUAN TRỌNG: Lưu giá tại thời điểm mua
     * Không dùng trực tiếp từ MenuItem vì giá có thể thay đổi sau này
     */
    @Column(nullable = false)
    private Long priceAtPurchase;

    /**
     * Tổng giá cho item này (quantity * priceAtPurchase)
     */
    @Column(nullable = false)
    private Long subtotal;


    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // ========== HELPER METHODS ==========

    /**
     * Tính tổng tiền cho item này
     */
    public void calculateSubtotal() {
        this.subtotal = this.quantity * this.priceAtPurchase;
    }

    /**
     * Getter cho menuItemId (để dễ serialize JSON)
     */
    public UUID getMenuItemId() {
        return menuItem != null ? menuItem.getId() : null;
    }

    /**
     * Getter cho menuItemName (để dễ hiển thị)
     */
    public String getMenuItemName() {
        return menuItem != null ? menuItem.getName() : null;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateSubtotal();
    }
}
