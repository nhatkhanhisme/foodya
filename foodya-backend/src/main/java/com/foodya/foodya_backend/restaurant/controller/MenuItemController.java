package com.foodya.foodya_backend. restaurant.controller;

import com. foodya.foodya_backend. restaurant.dto.MenuItemResponse;
import com.foodya. foodya_backend.restaurant.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io. swagger.v3.oas. annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu items APIs for mobile app (read-only)")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Operation(
        summary = "Get menu items with pagination",
        description = "Retrieve menu items for a restaurant with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Menu items retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<MenuItemResponse>> getMenuItems(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field (name, price, orderCount)", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<MenuItemResponse> response = menuItemService.getMenuItemsByRestaurant(
                restaurantId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all menu items",
        description = "Retrieve all active menu items for a restaurant (no pagination)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId) {

        List<MenuItemResponse> response = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get menu item by ID",
        description = "Retrieve detailed information about a specific menu item"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Menu item found",
            content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID")
            @PathVariable UUID menuItemId) {

        MenuItemResponse response = menuItemService. getMenuItemById(menuItemId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Search menu items",
        description = "Search menu items by keyword (name, description)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results returned")
    })
    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponse>> searchMenuItems(
            @Parameter(description = "Restaurant ID")
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items filtered successfully")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByCategory(
            @Parameter(description = "Restaurant ID")
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Popular items retrieved successfully")
    })
    @GetMapping("/popular")
    public ResponseEntity<List<MenuItemResponse>> getPopularMenuItems(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Number of items to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        List<MenuItemResponse> response = menuItemService.getPopularMenuItems(restaurantId, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get menu items by dietary preferences",
        description = "Filter menu items by vegetarian, vegan, or gluten-free options"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items filtered successfully")
    })
    @GetMapping("/dietary")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByDietaryPreferences(
            @Parameter(description = "Restaurant ID")
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
