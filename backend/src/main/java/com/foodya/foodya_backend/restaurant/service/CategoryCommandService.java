package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.CategoryRequest;
import com.foodya.foodya_backend.restaurant.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.model.Category;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.CategoryRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnershipService ownershipService;

    @Transactional
    public CategoryResponse createCategory(@NonNull UUID restaurantId, @NonNull CategoryRequest request) {
        log.info("Creating category '{}' for restaurant ID: {}", request.getName(), restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Restaurant not found with id: " + restaurantId));

        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN,
                    "You don't have permission to create categories for this restaurant");
        }
        if (categoryRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Category with name '" + request.getName() + "' already exists for this restaurant");
        }

        Category category = Category.builder()
                .name(request.getName())
                .restaurant(restaurant)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created with ID: {}", saved.getId());
        return CategoryResponse.fromEntity(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(
            @NonNull UUID categoryId, @NonNull UUID restaurantId, @NonNull CategoryRequest request) {
        log.info("Updating category ID: {} for restaurant ID: {}", categoryId, restaurantId);

        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN,
                    "You don't have permission to update categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Category not found with id: " + categoryId));

        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Category does not belong to this restaurant");
        }

        if (!category.getName().equals(request.getName())) {
            Category existing = categoryRepository.findByNameAndRestaurantId(request.getName(), restaurantId)
                    .orElse(null);
            if (existing != null && !existing.getId().equals(categoryId)) {
                throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                        "Category with name '" + request.getName() + "' already exists for this restaurant");
            }
        }

        category.setName(request.getName());
        log.info("Category updated: {}", categoryId);
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(@NonNull UUID categoryId, @NonNull UUID restaurantId) {
        log.info("Deleting category ID: {} for restaurant ID: {}", categoryId, restaurantId);

        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN,
                    "You don't have permission to delete categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Category not found with id: " + categoryId));

        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Category does not belong to this restaurant");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", categoryId);
    }
}
