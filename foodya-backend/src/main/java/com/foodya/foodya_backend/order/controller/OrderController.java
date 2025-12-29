package com.foodya.foodya_backend.order.controller;

import com.foodya.foodya_backend.order.dto.OrderRequest;
import com.foodya.foodya_backend.order. dto.OrderResponse;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order. service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses. ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order APIs for mobile app")
public class OrderController {

    private final OrderService orderService;

    // ========== 1. CREATE ORDER ==========

    @Operation(summary = "Create new order", description = "Customer creates a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Customer or Restaurant not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ========== 2. GET ORDER BY ID ==========

    @Operation(summary = "Get order details", description = "Get order by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable UUID id) {
        OrderResponse response = orderService. getOrderById(id);
        return ResponseEntity.ok(response);
    }

    // ========== 3. GET MY ORDERS (by customer) ==========

    @Operation(summary = "Get my orders", description = "Get all orders of a customer (newest first)")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<OrderResponse> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    // ========== 4. GET ACTIVE ORDERS ==========

    @Operation(
        summary = "Get active orders",
        description = "Get orders that are PENDING, PREPARING, or SHIPPING"
    )
    @GetMapping("/customer/{customerId}/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrders(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<OrderResponse> orders = orderService. getActiveOrdersByCustomer(customerId);
        return ResponseEntity. ok(orders);
    }

    // ========== 5. GET RESTAURANT ORDERS ==========

    @Operation(summary = "Get restaurant orders", description = "Get all orders of a restaurant (for merchant)")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {
        List<OrderResponse> orders = orderService.getOrdersByRestaurant(restaurantId);
        return ResponseEntity. ok(orders);
    }

    // ========== 6. UPDATE ORDER STATUS ==========

    @Operation(
        summary = "Update order status",
        description = "Update order status (PENDING → PREPARING → SHIPPING → DELIVERED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // ========== 7. CANCEL ORDER ==========

    @Operation(
        summary = "Cancel order",
        description = "Cancel order (only PENDING or PREPARING can be cancelled)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable UUID id,
            @Parameter(description = "Cancel reason") @RequestParam(required = false) String reason) {
        OrderResponse response = orderService.cancelOrder(id, reason);
        return ResponseEntity. ok(response);
    }

    // ========== 8. DELETE ORDER (Admin only) ==========

    @Operation(summary = "Delete order", description = "Permanently delete an order (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable UUID id) {
        orderService. deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
