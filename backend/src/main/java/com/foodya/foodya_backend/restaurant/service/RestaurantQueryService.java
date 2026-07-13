package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.model.RestaurantStatus;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
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
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantPopularityService restaurantPopularityService;

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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + id));
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
