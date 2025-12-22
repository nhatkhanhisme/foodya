package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.MenuItemRequest;
import com.foodya.foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.service.MenuItemService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu item management APIs for restaurants")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private OwnershipService ownershipService;

    @Operation(
        summary = "Create new menu item",
        description = "Add a new menu item to a restaurant's menu",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Menu item created successfully",
            content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate item name"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Valid @RequestBody MenuItemRequest request) {


        // Check if ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        MenuItemResponse response = menuItemService.createMenuItem(restaurantId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update menu item",
        description = "Update an existing menu item's details",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
        @ApiResponse(responseCode = "404", description = "Menu item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID", example = "1")
            @PathVariable UUID menuItemId,
            @Valid @RequestBody MenuItemRequest request) {

        // Check if ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        MenuItemResponse response = menuItemService.updateMenuItem(menuItemId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Soft delete menu item",
        description = "Hide menu item from menu (set isActive = false)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu item soft deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> softDeleteMenuItem(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID", example = "1")
            @PathVariable UUID menuItemId) {

        // Check if ownership
        if (!ownershipService.isAdmin() && !ownershipService.isRestaurantOwner(restaurantId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        menuItemService.softDeleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Hard delete menu item",
        description = "Permanently delete menu item from database (admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu item permanently deleted"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{menuItemId}/hard")
    public ResponseEntity<Void> hardDeleteMenuItem(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID", example = "1")
            @PathVariable UUID menuItemId) {

        // Check if ownership
        if (!ownershipService.isAdmin())  {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        menuItemService.hardDeleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get menu item by ID",
        description = "Retrieve detailed information about a specific menu item"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item found"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID", example = "1")
            @PathVariable UUID menuItemId) {

        MenuItemResponse response = menuItemService.getMenuItemById(menuItemId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all menu items (with pagination)",
        description = "Retrieve all active menu items for a restaurant with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<MenuItemResponse>> getMenuItems(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<MenuItemResponse> response = menuItemService.getMenuItemsByRestaurant(
                restaurantId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all menu items (no pagination)",
        description = "Retrieve all active menu items for a restaurant without pagination"
    )
    @GetMapping("/all")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId) {

        List<MenuItemResponse> response = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Search menu items",
        description = "Search menu items by name (case-insensitive)"
    )
    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponse>> searchMenuItems(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Search keyword", example = "pizza")
            @RequestParam String keyword) {

        List<MenuItemResponse> response = menuItemService.searchMenuItems(restaurantId, keyword);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get menu items by category",
        description = "Filter menu items by category"
    )
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByCategory(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Category name", example = "Main Course")
            @PathVariable String category) {

        List<MenuItemResponse> response = menuItemService.getMenuItemsByCategory(restaurantId, category);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get popular menu items",
        description = "Get the most ordered menu items for a restaurant"
    )
    @GetMapping("/popular")
    public ResponseEntity<List<MenuItemResponse>> getPopularMenuItems(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Number of items to return", example = "5")
            @RequestParam(defaultValue = "5") int limit) {

        List<MenuItemResponse> response = menuItemService.getPopularMenuItems(restaurantId, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Toggle menu item availability",
        description = "Enable or disable a menu item for ordering"
    )
    @PatchMapping("/{menuItemId}/toggle-availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID", example = "1")
            @PathVariable UUID menuItemId) {

        MenuItemResponse response = menuItemService.toggleAvailability(menuItemId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get menu items by dietary preferences",
        description = "Filter menu items by vegetarian, vegan, or gluten-free options"
    )
    @GetMapping("/dietary")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByDietaryPreferences(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Vegetarian items only", example = "true")
            @RequestParam(required = false) Boolean vegetarian,
            @Parameter(description = "Vegan items only", example = "false")
            @RequestParam(required = false) Boolean vegan,
            @Parameter(description = "Gluten-free items only", example = "false")
            @RequestParam(required = false) Boolean glutenFree) {

        List<MenuItemResponse> response = menuItemService.getMenuItemsByDietaryPreferences(
                restaurantId, vegetarian, vegan, glutenFree);
        return ResponseEntity.ok(response);
    }
}
