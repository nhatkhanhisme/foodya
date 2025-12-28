package com.foodya.foodya_backend. restaurant.controller;

import com. foodya.foodya_backend. restaurant.dto.RestaurantResponse;
import com.foodya. foodya_backend.restaurant.service.RestaurantService;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "Restaurant APIs for mobile app")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
        summary = "Get restaurants with filters",
        description = "Search and filter restaurants by keyword, cuisine, rating with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurants retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> getRestaurants(
            @Parameter(description = "Search keyword (searches in name, description, cuisine)", example = "Pizza")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Filter by cuisine type", example = "Italian")
            @RequestParam(required = false) String cuisine,

            @Parameter(description = "Filter by minimum rating (1.0 - 5.0)", example = "4.0")
            @RequestParam(required = false) Double minRating,

            @Parameter(description = "Sort by:  popular (default), rating, name", example = "popular")
            @RequestParam(required = false, defaultValue = "popular") String sortBy,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (default:  20)", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Page<RestaurantResponse> restaurants = restaurantService.getRestaurantsWithFilters(
                keyword, cuisine, minRating, sortBy, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
        summary = "Get restaurant by ID",
        description = "Retrieve detailed information about a specific restaurant"
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
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID id) {
        RestaurantResponse restaurant = restaurantService. getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @Operation(
        summary = "Get popular restaurants",
        description = "Retrieve the most popular restaurants based on reviews and ratings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Popular restaurants retrieved successfully"
        )
    })
    @GetMapping("/popular")
    public ResponseEntity<List<RestaurantResponse>> getPopularRestaurants(
            @Parameter(description = "Number of restaurants to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        List<RestaurantResponse> restaurants = restaurantService.getPopularRestaurants(limit);
        return ResponseEntity.ok(restaurants);
    }

    // ========== XÓA TẤT CẢ CÁC METHOD SAU ĐÂY ==========
    // ❌ searchRestaurants() - đã gộp vào getRestaurants()
    // ❌ getRestaurantsByCuisine() - đã gộp vào getRestaurants()
    // ❌ getRestaurantsByRating() - đã gộp vào getRestaurants()
    // ❌ deleteRestaurantById() - đã di chuyển sang AdminRestaurantController
}
