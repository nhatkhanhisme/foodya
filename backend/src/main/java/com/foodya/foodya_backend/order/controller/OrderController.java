package com.foodya.foodya_backend.order.controller;

import com.foodya.foodya_backend.order.dto.OrderRequest;
import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.service.OrderCommandService;
import com.foodya.foodya_backend.order.service.OrderQueryService;
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
@RequestMapping("/api/v1/customers/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Orders", description = "Customer Order APIs for mobile app")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @Operation(summary = "Create new order", description = "Customer creates a new order")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderCommandService.createOrder(authentication, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get my orders", description = "Get all orders of current customer (newest first)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderQueryService.getMyOrders(authentication));
    }

    @Operation(summary = "Get my active orders", description = "Get my orders that are PENDING, PREPARING, or SHIPPING")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active orders retrieved successfully")
    })
    @GetMapping("/me/active")
    public ResponseEntity<List<OrderResponse>> getMyActiveOrders(Authentication authentication) {
        return ResponseEntity.ok(orderQueryService.getMyActiveOrders(authentication));
    }

    @Operation(summary = "Cancel order", description = "Cancel order (only PENDING or PREPARING can be cancelled)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @Parameter(description = "Order ID") @PathVariable UUID id,
            @Parameter(description = "Cancel reason") @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(orderCommandService.cancelMyOrder(authentication, id, reason));
    }
}
