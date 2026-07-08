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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

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

    @Cacheable(value = "restaurant", key = "#id")
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(@NonNull UUID id) {
        log.info("Fetching restaurant with id: {}", id);
        return restaurantRepository.findById(id)
                .map(RestaurantResponse::fromEntity)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Restaurant not found with id: " + id));
    }

    @Cacheable(value = "popular-restaurants", key = "#limit")
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getPopularRestaurants(int limit) {
        log.info("Fetching top {} popular restaurants", limit);
        Pageable pageable = PageRequest.of(0, limit,
                Sort.by("totalReviews").descending().and(Sort.by("rating").descending()));
        return restaurantRepository.findByStatus(RestaurantStatus.APPROVED, pageable)
                .getContent().stream().map(RestaurantResponse::fromEntity).toList();
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
