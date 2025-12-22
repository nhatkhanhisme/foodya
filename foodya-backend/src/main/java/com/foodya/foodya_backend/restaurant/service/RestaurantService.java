package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.restaurant.dto.RestaurantMapper;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    /**
     * Get all active restaurants
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        log.info("Fetching all active restaurants");
        List<Restaurant> restaurants = restaurantRepository.findByIsActiveTrue();
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }

    /**
     * Get all restaurants including inactive (for admin)
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurantsIncludingInactive() {
        log.info("Fetching all restaurants including inactive");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }

    /**
     * Get restaurant by ID
     */
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(@NonNull UUID id) {
        log.info("Fetching restaurant with id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    /**
     * Search restaurants by name
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> searchRestaurants(String keyword) {
        log.info("Searching restaurants with keyword: {}", keyword);
        List<Restaurant> restaurants = restaurantRepository.searchByName(keyword);
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }

    /**
     * Get restaurants by cuisine
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByCuisine(String cuisine) {
        log.info("Fetching restaurants with cuisine: {}", cuisine);
        List<Restaurant> restaurants = restaurantRepository.findByCuisineAndIsActiveTrue(cuisine);
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }

    /**
     * Get popular restaurants
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getPopularRestaurants() {
        log.info("Fetching popular restaurants");
        List<Restaurant> restaurants = restaurantRepository.findPopularRestaurants();
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }

    /**
     * Get restaurants by rating
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByMinRating(Double minRating) {
        log.info("Fetching restaurants with rating >= {}", minRating);
        List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThanEqual(minRating);
        return restaurantMapper.toRestaurantResponseList(restaurants);
    }
    // Delete restaurant by ID (for admin)
    @Transactional(readOnly = false)
    public void deleteRestaurantById(@NonNull UUID id) {
        log.info("Deleting restaurant with id: {}", id);
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }
}
