package com.foodya. foodya_backend.admin.controller;

import com.foodya. foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya. foodya_backend.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org. springframework.security.access.prepost.PreAuthorize;
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

    private final RestaurantService restaurantService;

    @Operation(
        summary = "Get all restaurants including inactive",
        description = "Admin only - Retrieve all restaurants including inactive ones"
    )
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurantsIncludingInactive() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurantsIncludingInactive();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Approve restaurant", description = "Admin only - Approve a pending restaurant")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.approveRestaurant(id));
    }

    @Operation(summary = "Reject restaurant", description = "Admin only - Reject a pending restaurant")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<RestaurantResponse> rejectRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.rejectRestaurant(id));
    }

    @Operation(summary = "Suspend restaurant", description = "Admin only - Suspend restaurant for policy violation")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<RestaurantResponse> suspendRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.suspendRestaurant(id));
    }

    @Operation(summary = "Delete restaurant", description = "Admin only - Permanently delete a restaurant")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id) {
        restaurantService.deleteRestaurantById(id);
        return ResponseEntity.noContent().build();
    }
}
