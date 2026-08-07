package com.foodya.foodya_backend.catalog.application;

import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.catalog.domain.Restaurant;
import com.foodya.foodya_backend.catalog.domain.MenuItem;
import com.foodya.foodya_backend.catalog.persistence.RestaurantRepository;
import com.foodya.foodya_backend.catalog.persistence.MenuItemRepository;
import com.foodya.foodya_backend.identity.application.ProfileService;
import com.foodya.foodya_backend.identity.domain.Role;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final ProfileService profileService;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Check if current user owns the restaurant
     */
    public boolean isRestaurantOwner(@NonNull UUID restaurantId) {
        UUID currentUserId = profileService.getCurrentUserId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException( "Restaurant not found"));

        return restaurant.getOwnerId().equals(currentUserId);
    }

    /**
     * Check if current user owns the restaurant that contains the menu item
     */
    boolean isMenuItemOwner(@NonNull UUID menuItemId) {
        UUID currentUserId = profileService.getCurrentUserId();
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException( "Menu item not found"));

        return menuItem.getRestaurant().getOwnerId().equals(currentUserId);
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return profileService.getCurrentUserRole() == Role.ADMIN;
    }
}
