package com.foodya.foodya_backend.catalog.application;

import com.foodya.foodya_backend.common.exception.DuplicateResourceException;
import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.catalog.api.dto.MenuItemMapper;
import com.foodya.foodya_backend.catalog.api.dto.MenuItemRequest;
import com.foodya.foodya_backend.catalog.api.dto.MenuItemResponse;
import com.foodya.foodya_backend.catalog.domain.Category;
import com.foodya.foodya_backend.catalog.domain.MenuItem;
import com.foodya.foodya_backend.catalog.domain.Restaurant;
import com.foodya.foodya_backend.catalog.persistence.CategoryRepository;
import com.foodya.foodya_backend.catalog.persistence.MenuItemRepository;
import com.foodya.foodya_backend.catalog.persistence.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemMapper menuItemMapper;

    // Internal — returns entity for cross-service calls (OrderService etc.); never expose via controller
    @Transactional(readOnly = true)
    public MenuItem findById(@NonNull UUID menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found with id: " + menuItemId));
    }

    @CacheEvict(value = "menu-items-active", key = "#restaurantId")
    @Transactional
    public MenuItemResponse createMenuItem(@NonNull UUID restaurantId, MenuItemRequest request) {
        log.info("Creating menu item '{}' for restaurant ID: {}", request.getName(), restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        if (menuItemRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new DuplicateResourceException(
                    "Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
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
            throw new DuplicateResourceException(
                    "Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        menuItemMapper.updateMenuItemFromRequest(menuItem, request);

        if (request.getCategoryId() != null &&
                (menuItem.getCategory() == null || !menuItem.getCategory().getId().equals(request.getCategoryId()))) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
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
            throw new ResourceNotFoundException( "Menu item not found with id: " + menuItemId);
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

    @Cacheable(value = "menu-item", key = "#menuItemId", sync = true)
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(@NonNull UUID menuItemId) {
        log.info("Fetching menu item with ID: {}", menuItemId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
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
            throw new ResourceNotFoundException( "Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(menuItemMapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menu-items-active", key = "#restaurantId", sync = true)
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getActiveMenuItemsByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching active menu items for restaurant ID: {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException( "Restaurant not found with id: " + restaurantId);
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
