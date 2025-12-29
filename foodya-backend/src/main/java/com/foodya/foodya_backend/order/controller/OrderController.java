package com.foodya.foodya_backend.order.controller;

import com.foodya.foodya_backend.order.dto.OrderRequest;
import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Orders", description = "Order APIs for mobile app")
public class OrderController {

  private final OrderService orderService;

  // ========== 1. CREATE ORDER ==========

  @Operation(summary = "Create new order", description = "Customer creates a new order")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "404", description = "Customer or Restaurant not found")
  })
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      Authentication authentication,
      @Valid @RequestBody OrderRequest request) {
    OrderResponse response = orderService.createOrder(authentication, request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  // ========== 2. GET MY ORDERS (by customer) ==========

  @Operation(summary = "Get my orders", description = "Get all orders of current customer (newest first)")
  @GetMapping("/me")
  public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
    List<OrderResponse> orders = orderService.getMyOrders(authentication);
    return ResponseEntity.ok(orders);
  }

  @Operation(summary = "Get my active orders", description = "Get my orders that are PENDING, PREPARING, or SHIPPING")
  @GetMapping("/me/active")
  public ResponseEntity<List<OrderResponse>> getMyActiveOrders(Authentication authentication) {
    List<OrderResponse> orders = orderService.getMyActiveOrders(authentication);
    return ResponseEntity.ok(orders);
  }

  @Operation(summary = "Cancel order", description = "Cancel order (only PENDING or PREPARING can be cancelled)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
      @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  @PatchMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> cancelOrder(
      Authentication authentication,
      @Parameter(description = "Order ID") @PathVariable UUID id,
      @Parameter(description = "Cancel reason") @RequestParam(required = false) String reason) {
    OrderResponse response = orderService.cancelMyOrder(authentication, id, reason);
    return ResponseEntity.ok(response);
  }

}
