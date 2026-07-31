package com.foodya.foodya_backend.restaurant.api;

import com.foodya.foodya_backend.restaurant.api.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.api.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.application.RestaurantService;
import com.foodya.foodya_backend.user.application.UserService;
import com.foodya.foodya_backend.auth.domain.Role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
@Tag(name = "Merchant - Restaurant Management", description = "⚠️ Restaurant owner only - Manage your restaurants")
@SecurityRequirement(name = "bearerAuth")
public class MerchantRestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    private boolean isAdmin() {
        return userService.getCurrentUserRole() == Role.ADMIN;
    }

    @Operation(summary = "Get my restaurants", description = "Get all restaurants owned by current merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    @GetMapping("/me")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants() {
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwner(userService.getCurrentUserId()));
    }

    @Operation(summary = "Create new restaurant", description = "Merchant creates a new restaurant")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Restaurant created successfully. Phone number auto-normalized to +84...",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = restaurantService.createRestaurant(
                request, userService.getCurrentUserId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update restaurant", description = "Update restaurant information (owner only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant updated successfully")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id,
            @Valid @RequestBody RestaurantRequest request) {

        RestaurantResponse response = restaurantService.updateRestaurant(
                id, request, userService.getCurrentUserId(), isAdmin());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle restaurant open/close", description = "Open or close restaurant for orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Open/close state toggled"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<RestaurantResponse> toggleRestaurantStatus(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id) {

        RestaurantResponse response = restaurantService.toggleRestaurantStatus(
                id, userService.getCurrentUserId(), isAdmin());
        return ResponseEntity.ok(response);
    }
}
