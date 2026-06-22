package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Public Category APIs for mobile app")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get public categories by restaurant",
            description = "Retrieve all categories for a specific restaurant. " +
                    "Results are sorted by name."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found"
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getPublicCategories(
            @Parameter(description = "Restaurant ID")
            @PathVariable UUID restaurantId) {
        List<CategoryResponse> categories = categoryService.getPublicCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(categories);
    }
}

