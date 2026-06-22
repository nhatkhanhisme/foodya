
package com.foodya.foodya_backend.merchant.controller;

import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
@Tag(name = "Merchant - Order Management", description = "⚠️ Merchant  owner only - Manage your orders")
@SecurityRequirement(name = "bearerAuth")
public class MerchantOrderController {

  private final OrderService orderService;


  // ========== . GET RESTAURANT ORDERS ==========

  @Operation(summary = "Get restaurant orders", description = "Get all orders of a restaurant (for merchant)")
  @GetMapping("/restaurant/{restaurantId}")
  public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
      @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {
    List<OrderResponse> orders = orderService.getOrdersByRestaurant(restaurantId);
    return ResponseEntity.ok(orders);
  }

  // ========== 6. UPDATE ORDER STATUS ==========

  @Operation(summary = "Update order status", description = "Update order status (PENDING → PREPARING → SHIPPING → DELIVERED)")
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
  // ========== 2. GET ORDER BY ID ==========

  @Operation(summary = "Get order details", description = "Get order by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(
      @Parameter(description = "Order ID") @PathVariable UUID id) {
    OrderResponse response = orderService.getOrderById(id);
    return ResponseEntity.ok(response);
  }
}
