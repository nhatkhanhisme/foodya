package com.foodya.foodya_backend.merchant.controller;

import com.foodya.foodya_backend.restaurant.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.service.RestaurantService;
import com.foodya.foodya_backend.user.model.Role;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;

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
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
@Tag(name = "Merchant - Restaurant Management", description = "⚠️ Restaurant owner only - Manage your restaurants")
@SecurityRequirement(name = "BearerAuth")
public class MerchantRestaurantController {

  private final RestaurantService restaurantService;
  private final UserRepository userRepository;

  private User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private boolean isAdmin() {
    User user = getCurrentUser();
    return user.getRole() == Role.ADMIN;
  }

  @Operation(summary = "Get my restaurants", description = "Get all restaurants owned by current merchant")
  @GetMapping("/me")
  public ResponseEntity<List<RestaurantResponse>> getMyRestaurants() {
    User currentUser = getCurrentUser();
    List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByOwner(currentUser.getId());
    return ResponseEntity.ok(restaurants);
  }

  @Operation(summary = "Create new restaurant", description = "Merchant creates a new restaurant")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or duplicate name/phone")
  })
  @PostMapping
  public ResponseEntity<RestaurantResponse> createRestaurant(
      @Valid @RequestBody RestaurantRequest request) {

    User currentUser = getCurrentUser();
    RestaurantResponse response = restaurantService.createRestaurant(request, currentUser.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(summary = "Update restaurant", description = "Update restaurant information (owner only)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
      @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
      @ApiResponse(responseCode = "404", description = "Restaurant not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<RestaurantResponse> updateRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable UUID id,
      @Valid @RequestBody RestaurantRequest request) {

    User currentUser = getCurrentUser();
    RestaurantResponse response = restaurantService.updateRestaurant(
        id, request, currentUser.getId(), isAdmin());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Toggle restaurant open/close", description = "Open or close restaurant for orders")
  @PatchMapping("/{id}/toggle-status")
  public ResponseEntity<RestaurantResponse> toggleRestaurantStatus(
      @Parameter(description = "Restaurant ID") @PathVariable UUID id) {

    User currentUser = getCurrentUser();
    RestaurantResponse response = restaurantService.toggleRestaurantStatus(
        id, currentUser.getId(), isAdmin());
    return ResponseEntity.ok(response);
  }
}
