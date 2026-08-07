package com.foodya.foodya_backend.ordering.api.dto;

import com.foodya.foodya_backend.ordering.domain.Order;
import com.foodya.foodya_backend.ordering.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "Response containing order details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    @Schema(description = "Order ID")
    private UUID id;

    @Schema(description = "Customer ID")
    private UUID customerId;

    @Schema(description = "Customer name")
    private String customerName;

    @Schema(description = "Restaurant ID")
    private UUID restaurantId;

    @Schema(description = "Restaurant name")
    private String restaurantName;

    @Schema(description = "Restaurant image URL")
    private String restaurantImageUrl;

    @Schema(description = "Restaurant phone")
    private String restaurantPhone;

    @Schema(description = "List of order items")
    private List<OrderItemResponse> items = new ArrayList<>();

    @Schema(description = "Subtotal (before delivery fee)")
    private Long subtotal;

    @Schema(description = "Delivery fee")
    private Long deliveryFee;

    @Schema(description = "Total price (subtotal + delivery fee)")
    private Long totalPrice;

    @Schema(description = "Total number of items")
    private Integer totalItems;

    @Schema(description = "Order status", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Delivery address")
    private String deliveryAddress;

    @Schema(description = "Order notes")
    private String orderNotes;

    @Schema(description = "Cancel reason (if cancelled)")
    private String cancelReason;

    @Schema(description = "Order date")
    private Instant orderDate;

    @Schema(description = "Created at")
    private Instant createdAt;

    @Schema(description = "Updated at")
    private Instant updatedAt;

    @Schema(description = "Can user cancel this order?")
    private Boolean canCancel;

    public static OrderResponse fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());

        if (order.getCustomer() != null) {
            response.setCustomerName(order.getCustomer().getFullName());
        }

        if (order.getRestaurant() != null) {
            response.setRestaurantName(order.getRestaurant().getName());
            response.setRestaurantImageUrl(order.getRestaurant().getImageUrl());
            response.setRestaurantPhone(order.getRestaurant().getPhoneNumber());
        }

        if (order.getOrderItems() != null) {
            response.setItems(
                order.getOrderItems().stream()
                    .map(OrderItemResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }

        long subtotal = order.getTotalPrice() - order.getDeliveryFee();
        response.setSubtotal(subtotal);
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTotalPrice(order.getTotalPrice());
        response.setTotalItems(order.getTotalItems());

        response.setStatus(order.getStatus());

        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setOrderNotes(order.getOrderNotes());
        response.setCancelReason(order.getCancelReason());

        response.setOrderDate(order.getOrderDate());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        response.setCanCancel(order.isCancellable());

        return response;
    }

}
