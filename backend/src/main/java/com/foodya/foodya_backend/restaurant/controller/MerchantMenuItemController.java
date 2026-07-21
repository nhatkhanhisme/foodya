package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.MenuItemRequest;
import com.foodya.foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.service.MenuItemCommandService;
import com.foodya.foodya_backend.restaurant.service.MenuItemQueryService;
import com.foodya.foodya_backend.restaurant.service.OwnershipService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/merchant/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
@Tag(name = "Merchant - Menu Items", description = "⚠️ Restaurant owner only - Menu management")
@SecurityRequirement(name = "bearerAuth")
public class MerchantMenuItemController {

    private final MenuItemQueryService menuItemQueryService;
    private final MenuItemCommandService menuItemCommandService;
    private final OwnershipService ownershipService;

    @Operation(
        summary = "Get all menu items",
        description = "Get all menu items (active and inactive) for a restaurant owned by current merchant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(menuItemQueryService.getAllMenuItemsByRestaurant(restaurantId));
    }

    @Operation(
        summary = "Get active menu items",
        description = "Get only active menu items for a restaurant owned by current merchant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active menu items retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/active")
    public ResponseEntity<List<MenuItemResponse>> getActiveMenuItems(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(menuItemQueryService.getActiveMenuItemsByRestaurant(restaurantId));
    }

    @Operation(
        summary = "Create menu item",
        description = "Add a new menu item to your restaurant's menu"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Menu item created successfully",
            content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate item name"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Valid @RequestBody MenuItemRequest request) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        MenuItemResponse response = menuItemCommandService.createMenuItem(restaurantId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update menu item",
        description = "Update an existing menu item's details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Menu item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID") @PathVariable UUID menuItemId,
            @Valid @RequestBody MenuItemRequest request) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(menuItemCommandService.updateMenuItem(menuItemId, request));
    }

    @Operation(
        summary = "Delete menu item",
        description = "Soft delete a menu item (hide from menu)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID") @PathVariable UUID menuItemId) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        menuItemCommandService.softDeleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Toggle menu item availability",
        description = "Enable or disable a menu item for ordering"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability toggled successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - You don't own this restaurant"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @PatchMapping("/{menuItemId}/toggle-availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID") @PathVariable UUID menuItemId) {

        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(menuItemCommandService.toggleAvailability(menuItemId));
    }
}
