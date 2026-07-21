package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.service.MenuItemCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io. swagger.v3.oas. annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security. SecurityRequirement;
import io. swagger.v3.oas. annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework. security.access.prepost.PreAuthorize;
import org. springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Menu Items", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminMenuItemController {

    private final MenuItemCommandService menuItemService;

    @Operation(
        summary = "Permanently delete menu item",
        description = "Admin only - Hard delete menu item from database"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu item permanently deleted"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @DeleteMapping("/{menuItemId}/hard")
    public ResponseEntity<Void> hardDeleteMenuItem(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId,
            @Parameter(description = "Menu item ID")
            @PathVariable UUID menuItemId) {

        menuItemService.hardDeleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }
}
