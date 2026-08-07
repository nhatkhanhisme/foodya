package com.foodya.foodya_backend.catalog.application;

import com.foodya.foodya_backend.common.exception.DuplicateResourceException;
import com.foodya.foodya_backend.common.exception.ForbiddenException;
import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.common.exception.ValidationException;
import com.foodya.foodya_backend.catalog.api.dto.CategoryRequest;
import com.foodya.foodya_backend.catalog.api.dto.CategoryResponse;
import com.foodya.foodya_backend.catalog.domain.Category;
import com.foodya.foodya_backend.catalog.domain.Restaurant;
import com.foodya.foodya_backend.catalog.persistence.CategoryRepository;
import com.foodya.foodya_backend.catalog.persistence.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnershipService ownershipService;

    @Transactional
    public CategoryResponse createCategory(@NonNull UUID restaurantId, @NonNull CategoryRequest request) {
        log.info("Creating category '{}' for restaurant ID: {}", request.getName(), restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new ForbiddenException(
                    "You don't have permission to create categories for this restaurant");
        }
        if (categoryRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new DuplicateResourceException(
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
            throw new ForbiddenException(
                    "You don't have permission to update categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new ValidationException( "Category does not belong to this restaurant");
        }

        if (!category.getName().equals(request.getName())) {
            Category existing = categoryRepository.findByNameAndRestaurantId(request.getName(), restaurantId)
                    .orElse(null);
            if (existing != null && !existing.getId().equals(categoryId)) {
                throw new DuplicateResourceException(
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
            throw new ForbiddenException(
                    "You don't have permission to delete categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new ValidationException( "Category does not belong to this restaurant");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", categoryId);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getPublicCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching public categories for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException( "Restaurant not found with id: " + restaurantId);
        }
        List<Category> categories = categoryRepository.findPublicCategoriesByRestaurantId(restaurantId);
        return CategoryResponse.fromEntityList(categories);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching all categories for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException( "Restaurant not found with id: " + restaurantId);
        }
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new ForbiddenException(
                    "You don't have permission to view all categories for this restaurant");
        }
        return CategoryResponse.fromEntityList(categoryRepository.findAllByRestaurantId(restaurantId));
    }
}
