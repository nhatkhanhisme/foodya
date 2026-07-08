package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.CategoryResponse;
import com.foodya.foodya_backend.restaurant.model.Category;
import com.foodya.foodya_backend.restaurant.repository.CategoryRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

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
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnershipService ownershipService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getPublicCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching public categories for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + restaurantId);
        }
        List<Category> categories = categoryRepository.findPublicCategoriesByRestaurantId(restaurantId);
        return CategoryResponse.fromEntityList(categories);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching all categories for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + restaurantId);
        }
        if (!ownershipService.isRestaurantOwner(restaurantId) && !ownershipService.isAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN,
                    "You don't have permission to view all categories for this restaurant");
        }
        return CategoryResponse.fromEntityList(categoryRepository.findAllByRestaurantId(restaurantId));
    }
}
