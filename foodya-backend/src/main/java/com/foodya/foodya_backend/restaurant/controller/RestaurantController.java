package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "Restaurant management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
        summary = "Get all restaurants",
        description = "Public endpoint"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved restaurants",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @Operation(
        summary = "Get restaurant by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurant found",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @Operation(
        summary = "Search restaurants"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results returned"
        )
    })
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @Parameter(description = "Search keyword", example = "Italian")
            @RequestParam String keyword) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(keyword));
    }

    @Operation(
        summary = "Get restaurants by cuisine"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurants filtered by cuisine"
        )
    })
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByCuisine(
            @Parameter(description = "Cuisine type", example = "Italian")
            @PathVariable String cuisine) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCuisine(cuisine));
    }

    @Operation(
        summary = "Get popular restaurants"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Popular restaurants returned"
        )
    })
    @GetMapping("/popular")
    public ResponseEntity<List<RestaurantResponse>> getPopularRestaurants() {
        return ResponseEntity.ok(restaurantService.getPopularRestaurants());
    }

    @Operation(
        summary = "Get restaurants by minimum rating"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurants filtered by rating"
        )
    })
    @GetMapping("/rating")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByRating(
            @Parameter(description = "Minimum rating", example = "4.0")
            @RequestParam(defaultValue = "4.0") Double minRating) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMinRating(minRating));
    }

    @Operation(
        summary = "Delete restaurant by ID (Admin only)",
        description = "Delete a specific restaurant by its ID (admin only)",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurant deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found"
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantById(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable UUID id) {
        restaurantService.deleteRestaurantById(id);
        return ResponseEntity.ok().build();
    }
}
