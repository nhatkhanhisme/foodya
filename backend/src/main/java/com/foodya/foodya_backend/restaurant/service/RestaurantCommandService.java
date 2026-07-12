package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.model.RestaurantStatus;
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
public class RestaurantCommandService {

    private final RestaurantRepository restaurantRepository;

    // Internal — returns entity for cross-service calls (OrderService etc.); never expose via controller
    @Transactional(readOnly = true)
    public Restaurant findById(@NonNull UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found: " + id));
    }

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, @NonNull UUID ownerId) {
        log.info("Creating restaurant '{}' for owner ID: {}", request.getName(), ownerId);

        if (restaurantRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Restaurant with name '" + request.getName() + "' already exists");
        }

        String normalizedPhone = normalizePhone(request.getPhoneNumber());
        if (normalizedPhone != null && restaurantRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Restaurant with phone number '" + normalizedPhone + "' already exists");
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(normalizedPhone)
                .email(request.getEmail())
                .description(request.getDescription())
                .cuisine(request.getCuisine())
                .imageUrl(request.getImageUrl())
                .coverImageUrl(request.getCoverImageUrl())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .openingHours(request.getOpeningHours())
                .status(RestaurantStatus.PENDING)
                .isOpen(request.getIsOpen() != null ? request.getIsOpen() : true)
                .isFeatured(false)
                .rating(0.0)
                .totalReviews(0)
                .totalOrders(0)
                .orderCount(0)
                .averageOrderValue(0L)
                .ownerId(ownerId)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant created with ID: {}", saved.getId());
        return RestaurantResponse.fromEntity(saved);
    }

    // popular-restaurants not evicted here: name/description edits can tolerate
    // up to 5 minutes of staleness; ranking freshness comes from the ZSET buckets
    @CacheEvict(value = "restaurant", key = "#id")
    @Transactional
    public RestaurantResponse updateRestaurant(@NonNull UUID id, RestaurantRequest request,
            UUID currentUserId, boolean isAdmin) {
        log.info("Updating restaurant with ID: {}", id);

        Restaurant restaurant = findById(id);

        if (!isAdmin && !restaurant.getOwnerId().equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You don't have permission to update this restaurant");
        }
        if (!restaurant.getName().equals(request.getName()) && restaurantRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Restaurant with name '" + request.getName() + "' already exists");
        }

        String normalizedPhone = normalizePhone(request.getPhoneNumber());
        if (normalizedPhone != null && !normalizedPhone.equals(restaurant.getPhoneNumber())
                && restaurantRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE,
                    "Restaurant with phone number '" + normalizedPhone + "' already exists");
        }

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhoneNumber(normalizedPhone != null ? normalizedPhone : restaurant.getPhoneNumber());
        restaurant.setEmail(request.getEmail());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisine(request.getCuisine());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCoverImageUrl(request.getCoverImageUrl());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setOpeningHours(request.getOpeningHours());
        if (request.getMinimumOrder() != null) restaurant.setMinimumOrder(request.getMinimumOrder());
        if (request.getMaxDeliveryDistance() != null) restaurant.setMaxDeliveryDistance(request.getMaxDeliveryDistance());
        if (request.getIsOpen() != null) restaurant.setIsOpen(request.getIsOpen());

        log.info("Restaurant updated: {}", id);
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public void deleteRestaurantById(@NonNull UUID id) {
        log.info("Deleting restaurant with id: {}", id);
        if (!restaurantRepository.existsById(id)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public RestaurantResponse toggleRestaurantStatus(@NonNull UUID id, UUID currentUserId, boolean isAdmin) {
        log.info("Toggling status for restaurant ID: {}", id);
        Restaurant restaurant = findById(id);
        if (!isAdmin && !restaurant.getOwnerId().equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You don't have permission to update this restaurant");
        }
        restaurant.setIsOpen(!restaurant.getIsOpen());
        log.info("Restaurant status toggled to: {}", restaurant.getIsOpen() ? "OPEN" : "CLOSED");
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public RestaurantResponse approveRestaurant(@NonNull UUID id) {
        Restaurant restaurant = findById(id);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        log.info("Restaurant {} approved", id);
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public RestaurantResponse rejectRestaurant(@NonNull UUID id) {
        Restaurant restaurant = findById(id);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        log.info("Restaurant {} rejected", id);
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public RestaurantResponse suspendRestaurant(@NonNull UUID id) {
        Restaurant restaurant = findById(id);
        restaurant.setStatus(RestaurantStatus.SUSPENDED);
        log.info("Restaurant {} suspended", id);
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        return phone.trim();
    }
}
