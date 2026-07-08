package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.MenuItemMapper;
import com.foodya.foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya.foodya_backend.restaurant.repository.MenuItemRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemQueryService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;

    @Cacheable(value = "menu-item", key = "#menuItemId")
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(@NonNull UUID menuItemId) {
        log.info("Fetching menu item with ID: {}", menuItemId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Menu item not found with id: " + menuItemId));
        return menuItemMapper.toMenuItemResponse(menuItem);
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuItemsByRestaurant(
            @NonNull UUID restaurantId, int page, int size, String sortBy, String sortDirection) {

        log.info("Fetching menu items for restaurant ID: {} (page: {}, size: {}, sort: {} {})",
                restaurantId, page, size, sortBy, sortDirection);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return menuItemRepository
                .findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(restaurantId, pageable)
                .map(menuItemMapper::toMenuItemResponse);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItemsByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching all menu items (including inactive) for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(menuItemMapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menu-items-active", key = "#restaurantId")
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getActiveMenuItemsByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching active menu items for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findByRestaurantIdAndIsActiveTrue(restaurantId).stream()
                .map(menuItemMapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchMenuItems(@NonNull UUID restaurantId, String keyword) {
        log.info("Searching menu items for restaurant ID: {} with keyword: {}", restaurantId, keyword);
        return menuItemMapper.toMenuItemResponseList(
                menuItemRepository.searchByRestaurantAndName(restaurantId, keyword));
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(
            @NonNull UUID restaurantId, @NonNull UUID categoryId) {
        log.info("Fetching menu items for restaurant ID: {} with categoryId: {}", restaurantId, categoryId);
        return menuItemMapper.toMenuItemResponseList(
                menuItemRepository.findByRestaurantIdAndCategory_IdAndIsActiveTrue(restaurantId, categoryId));
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getPopularMenuItems(@NonNull UUID restaurantId, int limit) {
        log.info("Fetching popular menu items for restaurant ID: {} (limit: {})", restaurantId, limit);
        Pageable pageable = PageRequest.of(0, limit);
        return menuItemMapper.toMenuItemResponseList(
                menuItemRepository.findPopularItemsByRestaurant(restaurantId, pageable).getContent());
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByDietaryPreferences(
            UUID restaurantId, Boolean vegetarian, Boolean vegan, Boolean glutenFree) {
        log.info("Fetching menu items for restaurant ID: {} with dietary preferences - veg: {}, vegan: {}, gf: {}",
                restaurantId, vegetarian, vegan, glutenFree);
        return menuItemMapper.toMenuItemResponseList(
                menuItemRepository.findByDietaryPreferences(restaurantId, vegetarian, vegan, glutenFree));
    }
}
