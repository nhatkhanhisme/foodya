package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.MenuItemMapper;
import com.foodya.foodya_backend.restaurant.dto.MenuItemRequest;
import com.foodya.foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.model.Category;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.CategoryRepository;
import com.foodya.foodya_backend.restaurant.repository.MenuItemRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemCommandService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemMapper menuItemMapper;

    // Internal — returns entity for cross-service calls (OrderService etc.); never expose via controller
    @Transactional(readOnly = true)
    public MenuItem findById(@NonNull UUID menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Menu item not found with id: " + menuItemId));
    }

    @CacheEvict(value = "menu-items-active", key = "#restaurantId")
    @Transactional
    public MenuItemResponse createMenuItem(@NonNull UUID restaurantId, MenuItemRequest request) {
        log.info("Creating menu item '{}' for restaurant ID: {}", request.getName(), restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Restaurant not found with id: " + restaurantId));

        if (menuItemRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Category not found with id: " + request.getCategoryId()));

        MenuItem menuItem = menuItemMapper.toMenuItem(request);
        menuItem.setRestaurant(restaurant);
        menuItem.setCategory(category);
        menuItem.setIsActive(true);
        menuItem.setIsDeleted(false);
        menuItem.setOrderCount(0);

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Menu item created with ID: {}", saved.getId());
        return menuItemMapper.toMenuItemResponse(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "menu-item", key = "#menuItemId"),
            @CacheEvict(value = "menu-items-active", allEntries = true)
    })
    @Transactional
    public MenuItemResponse updateMenuItem(@NonNull UUID menuItemId, MenuItemRequest request) {
        log.info("Updating menu item with ID: {}", menuItemId);

        MenuItem menuItem = findById(menuItemId);

        if (!menuItem.getName().equals(request.getName()) &&
                menuItemRepository.existsByNameAndRestaurantId(request.getName(), menuItem.getRestaurant().getId())) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        menuItemMapper.updateMenuItemFromRequest(menuItem, request);

        if (request.getCategoryId() != null &&
                (menuItem.getCategory() == null || !menuItem.getCategory().getId().equals(request.getCategoryId()))) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                            "Category not found with id: " + request.getCategoryId()));
            menuItem.setCategory(category);
        }

        log.info("Menu item updated: {}", menuItemId);
        return menuItemMapper.toMenuItemResponse(menuItemRepository.save(menuItem));
    }

    @Caching(evict = {
            @CacheEvict(value = "menu-item", key = "#menuItemId"),
            @CacheEvict(value = "menu-items-active", allEntries = true)
    })
    @Transactional
    public void softDeleteMenuItem(@NonNull UUID menuItemId) {
        log.info("Soft deleting menu item with ID: {}", menuItemId);
        MenuItem menuItem = findById(menuItemId);
        menuItem.setIsActive(false);
        menuItem.setIsAvailable(false);
        menuItem.setIsDeleted(true);
        menuItemRepository.save(menuItem);
        log.info("Menu item soft deleted: {}", menuItemId);
    }

    @Caching(evict = {
            @CacheEvict(value = "menu-item", key = "#menuItemId"),
            @CacheEvict(value = "menu-items-active", allEntries = true)
    })
    @Transactional
    public void hardDeleteMenuItem(@NonNull UUID menuItemId) {
        log.info("Hard deleting menu item with ID: {}", menuItemId);
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Menu item not found with id: " + menuItemId);
        }
        menuItemRepository.deleteById(menuItemId);
        log.info("Menu item hard deleted: {}", menuItemId);
    }

    @Caching(evict = {
            @CacheEvict(value = "menu-item", key = "#menuItemId"),
            @CacheEvict(value = "menu-items-active", allEntries = true)
    })
    @Transactional
    public MenuItemResponse toggleAvailability(@NonNull UUID menuItemId) {
        log.info("Toggling availability for menu item ID: {}", menuItemId);
        MenuItem menuItem = findById(menuItemId);
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Menu item availability toggled to: {}", updated.getIsAvailable());
        return menuItemMapper.toMenuItemResponse(updated);
    }
}
