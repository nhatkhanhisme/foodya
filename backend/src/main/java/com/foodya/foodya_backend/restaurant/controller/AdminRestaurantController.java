package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.service.RestaurantCommandService;
import com.foodya.foodya_backend.restaurant.service.RestaurantQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Restaurant Management", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminRestaurantController {

    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantCommandService restaurantCommandService;

    @Operation(
        summary = "Get all restaurants including inactive",
        description = "Admin only - Retrieve all restaurants regardless of status (PENDING/APPROVED/REJECTED/SUSPENDED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurantsIncludingInactive() {
        return ResponseEntity.ok(restaurantQueryService.getAllRestaurantsIncludingInactive());
    }

    @Operation(summary = "Approve restaurant",
        description = "Admin only - Approve a pending restaurant. Approved restaurants become visible and orderable to customers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant approved"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<RestaurantResponse> approveRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id) {
        return ResponseEntity.ok(restaurantCommandService.approveRestaurant(id));
    }

    @Operation(summary = "Reject restaurant",
        description = "Admin only - Reject a pending restaurant with a reason (owner can fix and resubmit)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant rejected"),
        @ApiResponse(responseCode = "400", description = "Missing rejection reason"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PatchMapping("/{id}/reject")
    public ResponseEntity<RestaurantResponse> rejectRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id,
            @Parameter(description = "Why the restaurant was rejected") @RequestParam String reason) {
        return ResponseEntity.ok(restaurantCommandService.rejectRestaurant(id, reason));
    }

    @Operation(summary = "Suspend restaurant",
        description = "Admin only - Suspend restaurant for policy violation. Suspended restaurants are hidden from customers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant suspended"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<RestaurantResponse> suspendRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id) {
        return ResponseEntity.ok(restaurantCommandService.suspendRestaurant(id));
    }

    // No DELETE endpoint on purpose: BR-31 — restaurants leave the platform via
    // SUSPENDED, preserving order history and FK integrity
}
