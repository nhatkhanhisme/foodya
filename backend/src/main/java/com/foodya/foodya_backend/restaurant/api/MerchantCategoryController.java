package com.foodya.foodya_backend.restaurant.api;

import com.foodya.foodya_backend.restaurant.api.dto.CategoryRequest;
import com.foodya.foodya_backend.restaurant.api.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.application.CategoryCommandService;
import com.foodya.foodya_backend.restaurant.application.CategoryQueryService;
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
@PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
@Tag(name = "Merchant - Category Management", description = "Category management APIs for restaurant owners and admins")
@SecurityRequirement(name = "bearerAuth")
public class MerchantCategoryController {

    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;

    @Operation(
        summary = "Create category",
        description = "Create a new category for a restaurant. Business Rule: Category name must be unique within the restaurant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Validation failed or duplicate category name"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Valid @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryCommandService.createCategory(restaurantId, request), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get all categories (Owner View)",
        description = "Get all categories for a restaurant. This is for management purposes."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(categoryQueryService.getAllCategoriesByRestaurant(restaurantId));
    }

    @Operation(
        summary = "Update category",
        description = "Update category name. Business Rule: New name must be unique within the restaurant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Validation failed or duplicate category name"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
        @ApiResponse(responseCode = "404", description = "Category or restaurant not found")
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Category ID") @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryCommandService.updateCategory(categoryId, restaurantId, request));
    }

    @Operation(summary = "Delete category", description = "Delete a category permanently.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not restaurant owner"),
        @ApiResponse(responseCode = "404", description = "Category or restaurant not found")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Restaurant ID") @PathVariable UUID restaurantId,
            @Parameter(description = "Category ID") @PathVariable UUID categoryId) {
        categoryCommandService.deleteCategory(categoryId, restaurantId);
        return ResponseEntity.noContent().build();
    }
}
