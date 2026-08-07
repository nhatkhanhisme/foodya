package com.foodya.foodya_backend.ordering.api.dto;

import com.foodya.foodya_backend.ordering.domain.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response containing order item details")
@Data
public class OrderItemResponse {

    @Schema(description = "Order item ID")
    private UUID id;

    @Schema(description = "Menu item ID")
    private UUID menuItemId;

    @Schema(description = "Menu item name")
    private String menuItemName;

    @Schema(description = "Quantity ordered")
    private Integer quantity;

    @Schema(description = "Price at time of purchase")
    private Long priceAtPurchase;

    @Schema(description = "Subtotal (quantity × price)")
    private Long subtotal;

    @Schema(description = "Special instructions")
    private String specialInstructions;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Updated timestamp")
    private Instant updatedAt;

    // ========== CONSTRUCTORS ==========

    public OrderItemResponse() {
    }

    // ========== FACTORY METHOD ==========

    public static OrderItemResponse fromEntity(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setMenuItemId(item.getMenuItemId());
        response.setMenuItemName(item.getMenuItemName());
        response.setQuantity(item.getQuantity());
        response.setPriceAtPurchase(item.getPriceAtPurchase());
        response.setSubtotal(item.getSubtotal());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        return response;
    }

}
