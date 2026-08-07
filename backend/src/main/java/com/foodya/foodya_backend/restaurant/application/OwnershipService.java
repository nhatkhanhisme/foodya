package com.foodya.foodya_backend.restaurant.application;

import com.foodya.foodya_backend.shared.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.restaurant.domain.Restaurant;
import com.foodya.foodya_backend.restaurant.domain.MenuItem;
import com.foodya.foodya_backend.restaurant.persistence.RestaurantRepository;
import com.foodya.foodya_backend.restaurant.persistence.MenuItemRepository;
import com.foodya.foodya_backend.user.application.UserService;
import com.foodya.foodya_backend.auth.domain.Role;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final UserService userService;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Check if current user owns the restaurant
     */
    public boolean isRestaurantOwner(@NonNull UUID restaurantId) {
        UUID currentUserId = userService.getCurrentUserId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException( "Restaurant not found"));

        return restaurant.getOwnerId().equals(currentUserId);
    }

    /**
     * Check if current user owns the restaurant that contains the menu item
     */
    boolean isMenuItemOwner(@NonNull UUID menuItemId) {
        UUID currentUserId = userService.getCurrentUserId();
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException( "Menu item not found"));

        return menuItem.getRestaurant().getOwnerId().equals(currentUserId);
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return userService.getCurrentUserRole() == Role.ADMIN;
    }
}
