package com.foodya.foodya_backend.restaurant.dto;

import com.foodya.foodya_backend.restaurant.model.MenuItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuItemMapper {

    public MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .restaurantId(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null)
                .restaurantName(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getName() : null)
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .category(menuItem.getCategory())
                .isAvailable(menuItem.getIsAvailable())
                .isActive(menuItem.getIsActive())
                .preparationTime(menuItem.getPreparationTime())
                .calories(menuItem.getCalories())
                .isVegetarian(menuItem.getIsVegetarian())
                .isVegan(menuItem.getIsVegan())
                .isGlutenFree(menuItem.getIsGlutenFree())
                .isSpicy(menuItem.getIsSpicy())
                .orderCount(menuItem.getOrderCount())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }

    public List<MenuItemResponse> toMenuItemResponseList(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(this::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    public MenuItem toMenuItem(MenuItemRequest request) {
        if (request == null) {
            return null;
        }

        return MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .isAvailable(request.getIsAvailable())
                .preparationTime(request.getPreparationTime())
                .calories(request.getCalories())
                .isVegetarian(request.getIsVegetarian())
                .isVegan(request.getIsVegan())
                .isGlutenFree(request.getIsGlutenFree())
                .isSpicy(request.getIsSpicy())
                .build();
    }

    public void updateMenuItemFromRequest(MenuItem menuItem, MenuItemRequest request) {
        if (request == null || menuItem == null) {
            return;
        }

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsAvailable(request.getIsAvailable());
        menuItem.setPreparationTime(request.getPreparationTime());
        menuItem.setCalories(request.getCalories());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsVegan(request.getIsVegan());
        menuItem.setIsGlutenFree(request.getIsGlutenFree());
        menuItem.setIsSpicy(request.getIsSpicy());
    }
}
