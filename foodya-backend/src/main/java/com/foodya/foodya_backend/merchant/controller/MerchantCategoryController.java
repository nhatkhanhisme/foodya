package com.foodya.foodya_backend.merchant.controller;

import com.foodya.foodya_backend.restaurant.dto.CategoryRequest;
import com.foodya.foodya_backend.restaurant.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.service.CategoryService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/restaurants/{restaurantId}/categories")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
@Tag(name = "Merchant - Category Management", description = "Category management APIs for restaurant owners and admins")
@SecurityRequirement(name = "bearerAuth")
public class MerchantCategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Create category",
            description = "Create a new category for a restaurant. Only restaurant owner can create categories. " +
                    "Business Rule: Category name must be unique within the restaurant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation failed or duplicate category name"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(restaurantId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all categories (Owner View)",
            description = "Get all categories for a restaurant. This is for management purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {
        List<CategoryResponse> categories = categoryService.getAllCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Update category",
            description = "Update category name. " +
                    "Business Rule: New name must be unique within the restaurant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation failed or duplicate category name"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
            @ApiResponse(responseCode = "404", description = "Category or restaurant not found")
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Category ID") @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(categoryId, restaurantId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete category",
            description = "Delete a category permanently."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
            @ApiResponse(responseCode = "404", description = "Category or restaurant not found")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Category ID") @PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId, restaurantId);
        return ResponseEntity.noContent().build();
    }
}

