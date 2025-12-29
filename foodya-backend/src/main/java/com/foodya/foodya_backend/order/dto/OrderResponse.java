package com.foodya.foodya_backend.order.dto;

import com.foodya.foodya_backend.order.model.Order;
import com.foodya.foodya_backend.order.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream. Collectors;

@Schema(description = "Response containing order details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    // ========== BASIC INFO ==========

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

    // ========== ORDER ITEMS ==========

    @Schema(description = "List of order items")
    private List<OrderItemResponse> items = new ArrayList<>();

    // ========== PRICING ==========

    @Schema(description = "Subtotal (before delivery fee)")
    private Double subtotal;

    @Schema(description = "Delivery fee")
    private Double deliveryFee;

    @Schema(description = "Total price (subtotal + delivery fee)")
    private Double totalPrice;

    @Schema(description = "Total number of items")
    private Integer totalItems;

    // ========== STATUS ==========

    @Schema(description = "Order status", example = "PENDING")
    private OrderStatus status;


    // ========== DELIVERY INFO ==========

    @Schema(description = "Delivery address")
    private String deliveryAddress;

    @Schema(description = "Order notes")
    private String orderNotes;

    @Schema(description = "Cancel reason (if cancelled)")
    private String cancelReason;

    // ========== DATES ==========

    @Schema(description = "Order date")
    private LocalDateTime orderDate;

    @Schema(description = "Order date formatted", example = "28/12/2025 14:30")
    private String orderDateFormatted;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;

    // ========== ACTIONS (for mobile UI) ==========

    @Schema(description = "Can user cancel this order?")
    private Boolean canCancel;

    // ========== FACTORY METHOD ==========

    public static OrderResponse fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();

        // Basic info
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());

        // Customer info
        if (order.getCustomer() != null) {
            response.setCustomerName(order.getCustomer().getFullName());
        }

        // Restaurant info
        if (order.getRestaurant() != null) {
            response.setRestaurantName(order.getRestaurant().getName());
            response.setRestaurantImageUrl(order.getRestaurant().getImageUrl());
            response. setRestaurantPhone(order. getRestaurant().getPhoneNumber());
        }

        // Order items
        if (order.getOrderItems() != null) {
            response.setItems(
                order.getOrderItems().stream()
                    .map(OrderItemResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }

        // Pricing
        double subtotal = order.getTotalPrice() - order.getDeliveryFee();
        response.setSubtotal(subtotal);
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTotalPrice(order.getTotalPrice());
        response.setTotalItems(order. getTotalItems());

        // Status
        response.setStatus(order. getStatus());

        // Delivery info
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setOrderNotes(order.getOrderNotes());
        response.setCancelReason(order. getCancelReason());

        // Dates
        response.setOrderDate(order.getOrderDate());
        response.setOrderDateFormatted(formatDate(order.getOrderDate()));
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        // Actions
        response.setCanCancel(order.isCancellable());

        return response;
    }

    // ========== HELPER METHODS ==========

    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
