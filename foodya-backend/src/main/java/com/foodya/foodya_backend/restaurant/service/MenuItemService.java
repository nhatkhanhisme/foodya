package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.restaurant.dto.MenuItemMapper;
import com.foodya.foodya_backend.restaurant.dto.MenuItemRequest;
import com.foodya.foodya_backend.restaurant.dto.MenuItemResponse;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.MenuItemRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;

    /**
     * Create new menu item for a restaurant
     */
    @Transactional
    public MenuItemResponse createMenuItem(@NonNull UUID  restaurantId, MenuItemRequest request) {
        log.info("Creating menu item '{}' for restaurant ID: {}", request.getName(), restaurantId);

        // Check if restaurant exists
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        // Check if menu item name already exists for this restaurant
        if (menuItemRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new RuntimeException("Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        // Create menu item
        MenuItem menuItem = menuItemMapper.toMenuItem(request);
        menuItem.setRestaurant(restaurant);
        menuItem.setIsActive(true);
        menuItem.setOrderCount(0);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully with ID: {}", savedMenuItem.getId());

        return menuItemMapper.toMenuItemResponse(savedMenuItem);
    }

    /**
     * Update existing menu item
     */
    @Transactional
    public MenuItemResponse updateMenuItem(@NonNull UUID  menuItemId, MenuItemRequest request) {
        log.info("Updating menu item with ID: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));

        // Check if name is being changed and if it conflicts with another item
        if (!menuItem.getName().equals(request.getName())) {
            if (menuItemRepository.existsByNameAndRestaurantId(request.getName(), menuItem.getRestaurant().getId())) {
                throw new RuntimeException("Menu item with name '" + request.getName() + "' already exists for this restaurant");
            }
        }

        menuItemMapper.updateMenuItemFromRequest(menuItem, request);
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        log.info("Menu item updated successfully: {}", menuItemId);
        return menuItemMapper.toMenuItemResponse(updatedMenuItem);
    }

    /**
     * Soft delete menu item (set isActive = false)
     */
    // hidden in UI, but
    @Transactional
    public void softDeleteMenuItem(@NonNull UUID  menuItemId) {
        log.info("Soft deleting menu item with ID: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));

        menuItem.setIsActive(false);
        menuItem.setIsAvailable(false);
        menuItemRepository.save(menuItem);

        log.info("Menu item soft deleted successfully: {}", menuItemId);
    }

    /**
     * Hard delete menu item (permanent delete)
     */
    // Use by Admin
    @Transactional
    public void hardDeleteMenuItem(@NonNull UUID menuItemId) {
        log.info("Hard deleting menu item with ID: {}", menuItemId);

        if (!menuItemRepository.existsById(menuItemId)) {
            throw new RuntimeException("Menu item not found with id: " + menuItemId);
        }

        menuItemRepository.deleteById(menuItemId);
        log.info("Menu item hard deleted successfully: {}", menuItemId);
    }

    /**
     * Get menu item by ID
     */
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(@NonNull UUID menuItemId) {
        log.info("Fetching menu item with ID: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));

        return menuItemMapper.toMenuItemResponse(menuItem);
    }

    /**
     * Get all menu items for a restaurant (with pagination and sorting)
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuItemsByRestaurant(
            @NonNull UUID restaurantId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.info("Fetching menu items for restaurant ID: {} (page: {}, size: {}, sort: {} {})",
                restaurantId, page, size, sortBy, sortDirection);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<MenuItem> menuItemsPage = menuItemRepository.findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(restaurantId, pageable);

        return menuItemsPage.map(menuItemMapper::toMenuItemResponse);
    }

    /**
     * Get all active menu items for a restaurant (no pagination)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItemsByRestaurant(@NonNull UUID restaurantId) {
        log.info("Fetching all active menu items for restaurant ID: {}", restaurantId);

        List<MenuItem> menuItems = menuItemRepository.findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(restaurantId);
        return menuItemMapper.toMenuItemResponseList(menuItems);
    }

    /**
     * Search menu items by name
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchMenuItems(@NonNull UUID restaurantId, String keyword) {
        log.info("Searching menu items for restaurant ID: {} with keyword: {}", restaurantId, keyword);

        List<MenuItem> menuItems = menuItemRepository.searchByRestaurantAndName(restaurantId, keyword);
        return menuItemMapper.toMenuItemResponseList(menuItems);
    }

    /**
     * Get menu items by category
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(@NonNull UUID restaurantId, String category) {
        log.info("Fetching menu items for restaurant ID: {} with category: {}", restaurantId, category);

        List<MenuItem> menuItems = menuItemRepository.findByRestaurantIdAndCategoryAndIsActiveTrue(restaurantId, category);
        return menuItemMapper.toMenuItemResponseList(menuItems);
    }

    /**
     * Get popular menu items for a restaurant
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getPopularMenuItems(@NonNull UUID restaurantId, int limit) {
        log.info("Fetching popular menu items for restaurant ID: {} (limit: {})", restaurantId, limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<MenuItem> menuItems = menuItemRepository.findPopularItemsByRestaurant(restaurantId, pageable).getContent();
        return menuItemMapper.toMenuItemResponseList(menuItems);
    }

    /**
     * Toggle menu item availability
     */
    @Transactional
    public MenuItemResponse toggleAvailability(@NonNull UUID menuItemId) {
        log.info("Toggling availability for menu item ID: {}", menuItemId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));

        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        log.info("Menu item availability toggled to: {}", updatedMenuItem.getIsAvailable());
        return menuItemMapper.toMenuItemResponse(updatedMenuItem);
    }

    /**
     * Get menu items by dietary preferences
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByDietaryPreferences(
            UUID restaurantId,
            Boolean vegetarian,
            Boolean vegan,
            Boolean glutenFree) {

        log.info("Fetching menu items for restaurant ID: {} with dietary preferences - veg: {}, vegan: {}, gf: {}",
                restaurantId, vegetarian, vegan, glutenFree);

        List<MenuItem> menuItems = menuItemRepository.findByDietaryPreferences(
                restaurantId, vegetarian, vegan, glutenFree);

        return menuItemMapper.toMenuItemResponseList(menuItems);
    }
}
