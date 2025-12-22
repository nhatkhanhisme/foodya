package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;
import com.foodya.foodya_backend.restaurant.repository.MenuItemRepository;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Check if current user owns the restaurant
     */
    public boolean isRestaurantOwner(@NonNull UUID restaurantId) {
        User currentUser = getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return restaurant.getOwnerId().equals(currentUser.getId());
    }

    /**
     * Check if current user owns the restaurant that contains the menu item
     */
    public boolean isMenuItemOwner(@NonNull UUID menuItemId) {
        User currentUser = getCurrentUser();
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        return menuItem.getRestaurant().getOwnerId().equals(currentUser.getId());
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().name().equals("ADMIN");
    }
}
