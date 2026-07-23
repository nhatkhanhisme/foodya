package com.foodya.foodya_backend.order.api;

import com.foodya.foodya_backend.order.api.dto.OrderResponse;
import com.foodya.foodya_backend.order.domain.OrderStatus;
import com.foodya.foodya_backend.order.application.OrderCommandService;
import com.foodya.foodya_backend.order.application.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Admin - Orders Management", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @Operation(summary = "Search orders",
        description = "List orders with optional filters: status, restaurant, customer, and date range. All filters combine with AND.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(
            @Parameter(description = "Filter by order status") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Filter by restaurant ID") @RequestParam(required = false) UUID restaurantId,
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Orders created at/after this time (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "Orders created at/before this time (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(orderQueryService.searchOrders(status, restaurantId, customerId, startDate, endDate));
    }

    @Operation(summary = "Get order details", description = "Get any order by ID (no ownership restriction — admin scope)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable UUID id) {
        return ResponseEntity.ok(orderQueryService.getOrderById(id));
    }

    @Operation(summary = "Update order status",
        description = "Force a status transition. Must still follow the allowed flow: "
            + "AWAITING_PAYMENT → PENDING/CANCELLED; PENDING → CONFIRMED/REJECTED/CANCELLED; "
            + "CONFIRMED → READY_FOR_PICKUP; READY_FOR_PICKUP → PICKED_UP; PICKED_UP → DELIVERED; "
            + "any non-terminal state → CANCELLED. Invalid transitions return 400.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Transition not allowed from current status"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @Parameter(description = "Order ID") @PathVariable UUID id,
            @Parameter(description = "Target status") @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderCommandService.updateOrderStatus(id, status));
    }

    @Operation(summary = "Get restaurant revenue",
        description = "Total revenue (sum of order totals, in the smallest currency unit) for a restaurant within a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenue calculated successfully")
    })
    @GetMapping("/metrics/revenue")
    public ResponseEntity<Long> revenue(
            @Parameter(description = "Restaurant ID") @RequestParam UUID restaurantId,
            @Parameter(description = "Range start (ISO-8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "Range end (ISO-8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(orderQueryService.calculateRevenue(restaurantId, startDate, endDate));
    }

    // No DELETE endpoint on purpose: orders are financial history — terminal
    // states are CANCELLED/REJECTED/DELIVERED (§8.2), rows are never removed
}
