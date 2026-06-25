package com.foodya.foodya_backend.admin.controller;

import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Orders  Management", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<List<OrderResponse>> listOrders(
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) UUID restaurantId,
      @RequestParam(required = false) UUID customerId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
  ) {
    return ResponseEntity.ok(orderService.searchOrders(status, restaurantId, customerId, startDate, endDate));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderResponse> updateStatus(
      @PathVariable UUID id,
      @RequestParam OrderStatus status
  ) {
    return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
  }

  @GetMapping("/metrics/revenue")
  public ResponseEntity<Long> revenue(
      @RequestParam UUID restaurantId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
  ) {
    return ResponseEntity.ok(orderService.calculateRevenue(restaurantId, startDate, endDate));
  }
  // ========== 8. DELETE ORDER (Admin only) ==========

  @Operation(summary = "Delete order", description = "Permanently delete an order (Admin only)")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(
      @Parameter(description = "Order ID") @PathVariable UUID id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
