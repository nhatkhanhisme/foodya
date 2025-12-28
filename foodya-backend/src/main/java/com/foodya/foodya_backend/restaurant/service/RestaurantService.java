package com.foodya.foodya_backend.restaurant.service;

import com.foodya.foodya_backend.restaurant.dto.RestaurantMapper;
import com.foodya.foodya_backend.restaurant.dto.RestaurantRequest;
import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;
import com.foodya.foodya_backend.utils.exception.business.DuplicateResourceException;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;
import com.foodya.foodya_backend.utils.exception.security.UnauthorizedException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final RestaurantMapper restaurantMapper;

  /**
   * Get all active restaurants (for internal)
   */
  @Transactional(readOnly = true)
  public List<RestaurantResponse> getAllRestaurants() {
    log.info("Fetching all active restaurants");
    List<Restaurant> restaurants = restaurantRepository.findByIsActiveTrue();
    return restaurantMapper.toRestaurantResponseList(restaurants);
  }

  /**
   * Get restaurants with filters and pagination (for mobile app)
   */
  @Transactional(readOnly = true)
  public Page<RestaurantResponse> getRestaurantsWithFilters(
      String keyword,
      String cuisine,
      Double minRating,
      String sortBy,
      int page,
      int size) {

    log.info("Fetching restaurants - keyword: {}, cuisine: {}, minRating: {}, sortBy:  {}, page: {}, size:  {}",
        keyword, cuisine, minRating, sortBy, page, size);

    // Xử lý sorting
    Sort sort;
    switch (sortBy != null ? sortBy.toLowerCase() : "popular") {
      case "rating":
        sort = Sort.by("rating").descending();
        break;
      case "name":
        sort = Sort.by("name").ascending();
        break;
      case "popular":
      default:
        sort = Sort.by("totalReviews").descending().and(Sort.by("rating").descending());
        break;
    }

    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Restaurant> restaurants;

    // Nếu có bất kỳ filter nào, dùng query tổng hợp
    if ((keyword != null && !keyword.isBlank()) ||
        (cuisine != null && !cuisine.isBlank()) ||
        minRating != null) {

      restaurants = restaurantRepository.findByFilters(
          keyword != null && !keyword.isBlank() ? keyword : null,
          cuisine != null && !cuisine.isBlank() ? cuisine : null,
          minRating,
          pageable);
    } else {
      // Không có filter, lấy tất cả
      restaurants = restaurantRepository.findByIsActiveTrue(pageable);
    }

    return restaurants.map(restaurantMapper::toRestaurantResponse);
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
   * Get restaurant by ID - for mobile
   */
  @Transactional(readOnly = true)
  public RestaurantResponse getRestaurantById(@NonNull UUID id) {
    log.info("Fetching restaurant with id: {}", id);
    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    return restaurantMapper.toRestaurantResponse(restaurant);
  }

  /**
   * Get popular restaurants - for mobile
   */
  @Transactional(readOnly = true)
  public List<RestaurantResponse> getPopularRestaurants(int limit) {
    log.info("Fetching top {} popular restaurants", limit);
    Pageable pageable = PageRequest.of(0, limit,
        Sort.by("totalReviews").descending().and(Sort.by("rating").descending()));
    Page<Restaurant> restaurants = restaurantRepository.findByIsActiveTrue(pageable);
    return restaurantMapper.toRestaurantResponseList(restaurants.getContent());
  }

  // Delete restaurant by ID (for admin)
  @Transactional(readOnly = false)
  public void deleteRestaurantById(@NonNull UUID id) {
    log.info("Deleting restaurant with id: {}", id);
    if (!restaurantRepository.existsById(id)) {
      throw new ResourceNotFoundException("Restaurant not found with id: " + id);
    }
    restaurantRepository.deleteById(id);
  }

  @Transactional
  public RestaurantResponse createRestaurant(RestaurantRequest request,@NonNull UUID ownerId) {
    log.info("Creating restaurant '{}' for owner ID:  {}", request.getName(), ownerId);

    // Validation
    if (restaurantRepository.existsByName(request.getName())) {
      throw new DuplicateResourceException("Restaurant with name '" + request.getName() + "' already exists");
    }
    if (restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new DuplicateResourceException("Restaurant with phone number '" + request.getPhoneNumber() + "' already exists");
    }

    // Build restaurant entity
    Restaurant restaurant = Restaurant.builder()
        // Basic Information
        .name(request.getName())
        .address(request.getAddress())
        .phoneNumber(request.getPhoneNumber())
        .email(request.getEmail())
        .description(request.getDescription())
        .cuisine(request.getCuisine())

        // Media
        .imageUrl(request.getImageUrl())
        .coverImageUrl(request.getCoverImageUrl())

        // Operating Hours
        .openingTime(request.getOpeningTime())
        .closingTime(request.getClosingTime())
        .openingHours(request.getOpeningHours())

        // Delivery Information
        // Status
        .isOpen(request.getIsOpen() != null ? request.getIsOpen() : true)
        .isActive(true)
        .isVerified(false) // Admin sẽ verify sau
        .isFeatured(false)

        // Initial values
        .rating(0.0)
        .totalReviews(0)
        .totalOrders(0)
        .orderCount(0)
        .averageOrderValue(0.0)

        // Owner
        .ownerId(ownerId)
        .build();

    Restaurant savedRestaurant = restaurantRepository.save(restaurant);
    log.info("Restaurant created successfully with ID: {}", savedRestaurant.getId());

    return RestaurantResponse.fromEntity(savedRestaurant);
  }

  @Transactional
  public RestaurantResponse updateRestaurant(@NonNull UUID id, RestaurantRequest request,
      UUID currentUserId, boolean isAdmin) {
    log.info("Updating restaurant with ID: {}", id);

    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

    // Check ownership
    if (!isAdmin && !restaurant.getOwnerId().equals(currentUserId)) {
      throw new UnauthorizedException("You don't have permission to update this restaurant");
    }

    // Check duplicates
    if (!restaurant.getName().equals(request.getName())) {
      if (restaurantRepository.existsByName(request.getName())) {
        throw new DuplicateResourceException("Restaurant with name '" + request.getName() + "' already exists");
      }
    }
    if (!restaurant.getPhoneNumber().equals(request.getPhoneNumber())) {
      if (restaurantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
        throw new DuplicateResourceException("Restaurant with phone number '" + request.getPhoneNumber() + "' already exists");
      }
    }

    // Update all fields
    restaurant.setName(request.getName());
    restaurant.setAddress(request.getAddress());
    restaurant.setPhoneNumber(request.getPhoneNumber());
    restaurant.setEmail(request.getEmail());
    restaurant.setDescription(request.getDescription());
    restaurant.setCuisine(request.getCuisine());
    restaurant.setImageUrl(request.getImageUrl());
    restaurant.setCoverImageUrl(request.getCoverImageUrl());
    restaurant.setOpeningTime(request.getOpeningTime());
    restaurant.setClosingTime(request.getClosingTime());
    restaurant.setOpeningHours(request.getOpeningHours());

    if (request.getMinimumOrder() != null)
      restaurant.setMinimumOrder(request.getMinimumOrder());
    if (request.getMaxDeliveryDistance() != null)
      restaurant.setMaxDeliveryDistance(request.getMaxDeliveryDistance());
    if (request.getIsOpen() != null)
      restaurant.setIsOpen(request.getIsOpen());

    Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
    log.info("Restaurant updated successfully: {}", id);

    return RestaurantResponse.fromEntity(updatedRestaurant);
  }

  /**
   * Get restaurants by owner (for merchant dashboard)
   */
  @Transactional(readOnly = true)
  public List<RestaurantResponse> getRestaurantsByOwner(UUID ownerId) {
    log.info("Fetching restaurants for owner ID: {}", ownerId);
    List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
    return restaurants.stream()
        .map(RestaurantResponse::fromEntity)
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Toggle restaurant open/close status
   */
  @Transactional
  public RestaurantResponse toggleRestaurantStatus(@NonNull UUID id, UUID currentUserId, boolean isAdmin) {
    log.info("Toggling status for restaurant ID: {}", id);

    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

    // Check ownership
    if (!isAdmin && !restaurant.getOwnerId().equals(currentUserId)) {
      throw new UnauthorizedException("You don't have permission to update this restaurant");
    }

    restaurant.setIsOpen(!restaurant.getIsOpen());
    Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

    log.info("Restaurant status toggled to: {}", updatedRestaurant.getIsOpen() ? "OPEN" : "CLOSED");
    return RestaurantResponse.fromEntity(updatedRestaurant);
  }

}
