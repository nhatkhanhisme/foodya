package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.restaurant.dto.CategoryRequest;
import com.foodya.foodya_backend.restaurant.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.model.Category;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.CategoryRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;
import com.foodya.foodya_backend.utils.exception.business.BadRequestException;
import com.foodya.foodya_backend.utils.exception.business.DuplicateResourceException;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;
import com.foodya.foodya_backend.utils.exception.security.UnauthorizedException;

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

    // ========== OWNER/ADMIN METHODS ==========

    /**
     * Create category mới cho restaurant
     * Business Rule: Tên không được trùng trong cùng một nhà hàng
     */
    @Transactional
    public CategoryResponse createCategory(@NonNull UUID restaurantId, @NonNull CategoryRequest request) {
        log.info("Creating category '{}' for restaurant ID: {}", request.getName(), restaurantId);

        // Check if restaurant exists
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        // Check ownership
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create categories for this restaurant");
        }

        // Validate: Tên không được trùng trong cùng một nhà hàng
        if (categoryRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists for this restaurant");
        }

        // Create category
        Category category = Category.builder()
                .name(request.getName())
                .restaurant(restaurant)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return CategoryResponse.fromEntity(savedCategory);
    }

    /**
     * Update category
     * Business Rule: Tên mới không được trùng với category khác trong cùng restaurant
     */
    @Transactional
    public CategoryResponse updateCategory(@NonNull UUID categoryId, @NonNull UUID restaurantId, @NonNull CategoryRequest request) {
        log.info("Updating category ID: {} for restaurant ID: {}", categoryId, restaurantId);

        // Check ownership
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // Verify category belongs to restaurant
        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new BadRequestException("Category does not belong to this restaurant");
        }

        // Validate: Nếu đổi tên, tên mới không được trùng
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
                // Kiểm tra xem có phải chính category này không
                Category existingCategory = categoryRepository.findByNameAndRestaurantId(request.getName(), restaurantId)
                        .orElse(null);
                if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
                    throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists for this restaurant");
                }
            }
        }

        // Update category
        category.setName(request.getName());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", categoryId);

        return CategoryResponse.fromEntity(updatedCategory);
    }

    /**
     * Delete category
     */
    @Transactional
    public void deleteCategory(@NonNull UUID categoryId, @NonNull UUID restaurantId) {
        log.info("Deleting category ID: {} for restaurant ID: {}", categoryId, restaurantId);

        // Check ownership
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to delete categories for this restaurant");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // Verify category belongs to restaurant
        if (!category.getRestaurantId().equals(restaurantId)) {
            throw new BadRequestException("Category does not belong to this restaurant");
        }

        // Delete category
        categoryRepository.delete(category);

        log.info("Category deleted successfully: {}", categoryId);
    }

    /**
     * Get All categories của restaurant (Owner View)
     * Bao gồm tất cả categories để quản lý
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching all categories for restaurant ID: {}", restaurantId);

        // Check if restaurant exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        // Check ownership (optional - có thể cho phép admin xem)
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view all categories for this restaurant");
        }

        List<Category> categories = categoryRepository.findAllByRestaurantId(restaurantId);
        return CategoryResponse.fromEntityList(categories);
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Get Public Categories của restaurant
     * Lấy tất cả categories của restaurant (public view)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getPublicCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching public categories for restaurant ID: {}", restaurantId);

        // Check if restaurant exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        List<Category> categories = categoryRepository.findPublicCategoriesByRestaurantId(restaurantId);
        return CategoryResponse.fromEntityList(categories);
    }
}

