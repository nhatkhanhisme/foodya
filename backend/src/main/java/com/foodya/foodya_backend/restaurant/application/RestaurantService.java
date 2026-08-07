package com.foodya.foodya_backend.restaurant.application;

import com.foodya.foodya_backend.shared.exception.DuplicateResourceException;
import com.foodya.foodya_backend.shared.exception.ForbiddenException;
import com.foodya.foodya_backend.shared.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.shared.exception.ValidationException;
import com.foodya.foodya_backend.restaurant.api.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.api.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.domain.Restaurant;
import com.foodya.foodya_backend.restaurant.domain.RestaurantStatus;
import com.foodya.foodya_backend.restaurant.persistence.RestaurantRepository;

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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantPopularityService restaurantPopularityService;

    // Internal — returns entity for cross-service calls (OrderService etc.); never expose via controller
    @Transactional(readOnly = true)
    public Restaurant findById(@NonNull UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( "Restaurant not found: " + id));
    }

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, @NonNull UUID ownerId) {
        log.info("Creating restaurant '{}' for owner ID: {}", request.getName(), ownerId);

        if (restaurantRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Restaurant with name '" + request.getName() + "' already exists");
        }

        String normalizedPhone = normalizePhone(request.getPhoneNumber());
        if (normalizedPhone != null && restaurantRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new DuplicateResourceException(
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
            throw new ForbiddenException( "You don't have permission to update this restaurant");
        }
        if (!restaurant.getName().equals(request.getName()) && restaurantRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Restaurant with name '" + request.getName() + "' already exists");
        }

        String normalizedPhone = normalizePhone(request.getPhoneNumber());
        if (normalizedPhone != null && !normalizedPhone.equals(restaurant.getPhoneNumber())
                && restaurantRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new DuplicateResourceException(
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
    public RestaurantResponse toggleRestaurantStatus(@NonNull UUID id, UUID currentUserId, boolean isAdmin) {
        log.info("Toggling status for restaurant ID: {}", id);
        Restaurant restaurant = findById(id);
        if (!isAdmin && !restaurant.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException( "You don't have permission to update this restaurant");
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
        restaurant.setRejectionReason(null);
        log.info("Restaurant {} approved", id);
        return RestaurantResponse.fromEntity(restaurantRepository.save(restaurant));
    }

    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#id"),
            @CacheEvict(value = "popular-restaurants", allEntries = true)
    })
    @Transactional
    public RestaurantResponse rejectRestaurant(@NonNull UUID id, String reason) {
        // UC-A02: the owner must be told why, so they can fix and resubmit (BR-31)
        if (reason == null || reason.isBlank()) {
            throw new ValidationException( "A reason is required when rejecting a restaurant");
        }
        Restaurant restaurant = findById(id);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurant.setRejectionReason(reason);
        log.info("Restaurant {} rejected: {}", id, reason);
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

    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getRestaurantsWithFilters(
            String keyword, String cuisine, Double minRating, String sortBy, int page, int size) {

        log.info("Fetching restaurants - keyword: {}, cuisine: {}, minRating: {}, sortBy: {}, page: {}, size: {}",
                keyword, cuisine, minRating, sortBy, page, size);

        Sort sort = switch (sortBy != null ? sortBy.toLowerCase() : "popular") {
            case "rating" -> Sort.by("rating").descending();
            case "name" -> Sort.by("name").ascending();
            default -> Sort.by("totalReviews").descending().and(Sort.by("rating").descending());
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Restaurant> restaurants;

        if ((keyword != null && !keyword.isBlank()) || (cuisine != null && !cuisine.isBlank()) || minRating != null) {
            restaurants = restaurantRepository.findByFilters(
                    keyword != null && !keyword.isBlank() ? keyword : null,
                    cuisine != null && !cuisine.isBlank() ? cuisine : null,
                    minRating,
                    RestaurantStatus.APPROVED,
                    pageable);
        } else {
            restaurants = restaurantRepository.findByStatus(RestaurantStatus.APPROVED, pageable);
        }

        return restaurants.map(RestaurantResponse::fromEntity);
    }

    @Cacheable(value = "restaurant", key = "#id", sync = true)
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(@NonNull UUID id) {
        log.info("Fetching restaurant with id: {}", id);
        return restaurantRepository.findById(id)
                .map(RestaurantResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException( "Restaurant not found with id: " + id));
    }

    @Cacheable(value = "popular-restaurants", key = "#limit", sync = true)
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getPopularRestaurants(int limit) {
        log.info("Fetching top {} popular restaurants", limit);

        List<UUID> topIds = restaurantPopularityService.getTopPopularRestaurants(limit);
        if (topIds.isEmpty()) {
            return fallbackPopularRestaurants(limit, Set.of());
        }

        Map<UUID, Restaurant> approvedById = restaurantRepository.findAllById(topIds).stream()
                .filter(r -> r.getStatus() == RestaurantStatus.APPROVED)
                .collect(Collectors.toMap(Restaurant::getId, Function.identity()));

        // findAllById does not preserve order, so re-sort by ZSET ranking
        List<RestaurantResponse> ranked = topIds.stream()
                .map(approvedById::get)
                .filter(Objects::nonNull)
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());

        if (ranked.size() < limit) {
            ranked.addAll(fallbackPopularRestaurants(limit - ranked.size(), approvedById.keySet()));
        }
        return ranked;
    }

    private List<RestaurantResponse> fallbackPopularRestaurants(int limit, Set<UUID> excludeIds) {
        Pageable pageable = PageRequest.of(0, limit + excludeIds.size(),
                Sort.by("totalReviews").descending().and(Sort.by("rating").descending()));
        return restaurantRepository.findByStatus(RestaurantStatus.APPROVED, pageable)
                .getContent().stream()
                .filter(r -> !excludeIds.contains(r.getId()))
                .limit(limit)
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurantsIncludingInactive() {
        log.info("Fetching all restaurants including inactive");
        return restaurantRepository.findAll().stream().map(RestaurantResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByOwner(UUID ownerId) {
        log.info("Fetching restaurants for owner ID: {}", ownerId);
        return restaurantRepository.findByOwnerId(ownerId).stream()
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
