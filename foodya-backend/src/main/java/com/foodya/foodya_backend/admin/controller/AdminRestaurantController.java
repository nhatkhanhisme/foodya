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
@SecurityRequirement(name = "BearerAuth")
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

    @Operation(
        summary = "Delete restaurant",
        description = "Admin only - Permanently delete a restaurant"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID id) {
        restaurantService.deleteRestaurantById(id);
        return ResponseEntity.noContent().build();
    }
}
