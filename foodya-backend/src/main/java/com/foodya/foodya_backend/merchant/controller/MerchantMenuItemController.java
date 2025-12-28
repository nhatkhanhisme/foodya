package com.foodya. foodya_backend.merchant.controller;

import com.foodya. foodya_backend.restaurant.dto.MenuItemRequest;
import com.foodya. foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.service.MenuItemService;
import com.foodya.foodya_backend.restaurant.service. OwnershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io. swagger.v3.oas. annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
@Tag(name = "Merchant - Menu Items", description = "⚠️ Restaurant owner only - Menu management")
@SecurityRequirement(name = "BearerAuth")
public class MerchantMenuItemController {

    private final MenuItemService menuItemService;
    private final OwnershipService ownershipService;

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
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Valid @RequestBody MenuItemRequest request) {

        // Check ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        MenuItemResponse response = menuItemService. createMenuItem(restaurantId, request);
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
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID")
            @PathVariable UUID menuItemId,
            @Valid @RequestBody MenuItemRequest request) {

        // Check ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        MenuItemResponse response = menuItemService.updateMenuItem(menuItemId, request);
        return ResponseEntity.ok(response);
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
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID")
            @PathVariable UUID menuItemId) {

        // Check ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        menuItemService.softDeleteMenuItem(menuItemId);
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
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID")
            @PathVariable UUID menuItemId) {

        // Check ownership
        if (!ownershipService.isAdmin() && !ownershipService. isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        MenuItemResponse response = menuItemService. toggleAvailability(menuItemId);
        return ResponseEntity.ok(response);
    }
}
