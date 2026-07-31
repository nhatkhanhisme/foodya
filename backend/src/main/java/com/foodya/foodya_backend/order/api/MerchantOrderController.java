package com.foodya.foodya_backend.order.api;

import com.foodya.foodya_backend.order.api.dto.OrderResponse;
import com.foodya.foodya_backend.order.domain.OrderStatus;
import com.foodya.foodya_backend.order.application.OrderService;
import com.foodya.foodya_backend.restaurant.application.OwnershipService;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
@Tag(name = "Merchant - Order Management", description = "⚠️ Merchant owner only - Manage your orders")
@SecurityRequirement(name = "bearerAuth")
public class MerchantOrderController {

    // UC-R05/R06: the only transitions a merchant may trigger; shipper and
    // customer transitions (PICKED_UP, DELIVERED, CANCELLED) go through their own APIs
    private static final Set<OrderStatus> MERCHANT_SETTABLE_STATUSES =
            Set.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.READY_FOR_PICKUP);

    private final OrderService orderService;
    private final OwnershipService ownershipService;

    private void verifyRestaurantAccess(UUID restaurantId) {
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            throw new AccessDeniedException("You do not own this restaurant");
        }
    }

    @Operation(summary = "Get restaurant orders", description = "Get all orders of a restaurant you own")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not the owner of this restaurant")
    })
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {
        verifyRestaurantAccess(restaurantId);
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @Operation(summary = "Update order status",
        description = "Confirm/reject a pending order or advance preparation. Allowed transitions: "
            + "PENDING → CONFIRMED/REJECTED; CONFIRMED → READY_FOR_PICKUP. "
            + "REJECTED requires a reason. Invalid transitions return 400.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Transition not allowed, or missing rejection reason"),
        @ApiResponse(responseCode = "403", description = "Not the owner, or status not settable by merchants"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable UUID id,
            @Parameter(description = "New status (CONFIRMED, REJECTED, READY_FOR_PICKUP)") @RequestParam OrderStatus status,
            @Parameter(description = "Reason — required when rejecting") @RequestParam(required = false) String reason) {
        if (!MERCHANT_SETTABLE_STATUSES.contains(status)) {
            throw new AccessDeniedException("Merchants cannot set status " + status);
        }
        OrderResponse order = orderService.getOrderById(id);
        verifyRestaurantAccess(order.getRestaurantId());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, reason));
    }

    @Operation(summary = "Get order details", description = "Get an order of a restaurant you own")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not the owner of this restaurant"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable UUID id) {
        OrderResponse order = orderService.getOrderById(id);
        verifyRestaurantAccess(order.getRestaurantId());
        return ResponseEntity.ok(order);
    }
}
