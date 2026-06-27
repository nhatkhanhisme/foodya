package com.foodya.foodya_backend.merchant.controller;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.service.RestaurantCommandService;
import com.foodya.foodya_backend.restaurant.service.RestaurantQueryService;
import com.foodya.foodya_backend.user.model.Role;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

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
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantCommandService restaurantCommandService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }

    private boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    @Operation(summary = "Get my restaurants", description = "Get all restaurants owned by current merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    @GetMapping("/me")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(restaurantQueryService.getRestaurantsByOwner(currentUser.getId()));
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
        User currentUser = getCurrentUser();
        RestaurantResponse response = restaurantCommandService.createRestaurant(request, currentUser.getId());
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

        User currentUser = getCurrentUser();
        RestaurantResponse response = restaurantCommandService.updateRestaurant(
                id, request, currentUser.getId(), isAdmin());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle restaurant open/close", description = "Open or close restaurant for orders")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<RestaurantResponse> toggleRestaurantStatus(
            @Parameter(description = "Restaurant ID") @PathVariable UUID id) {

        User currentUser = getCurrentUser();
        RestaurantResponse response = restaurantCommandService.toggleRestaurantStatus(
                id, currentUser.getId(), isAdmin());
        return ResponseEntity.ok(response);
    }
}
