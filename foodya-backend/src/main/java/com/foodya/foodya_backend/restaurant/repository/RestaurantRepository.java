package com.foodya.foodya_backend.restaurant.repository;

import com.foodya.foodya_backend.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    // Find by name
    Optional<Restaurant> findByName(String name);

    // Find by owner
    List<Restaurant> findByOwnerId(UUID ownerId);

    // Find active restaurants
    List<Restaurant> findByIsActiveTrue();

    // Find open restaurants
    List<Restaurant> findByIsOpenTrue();

    // Find by cuisine type
    List<Restaurant> findByCuisine(String cuisine);

    // Find by cuisine and active
    List<Restaurant> findByCuisineAndIsActiveTrue(String cuisine);

    // Search by name (case-insensitive)
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.isActive = true")
    List<Restaurant> searchByName(@Param("keyword") String keyword);

    // Find by rating greater than
    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :minRating AND r.isActive = true ORDER BY r.rating DESC")
    List<Restaurant> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);

    // Find popular restaurants (by total reviews)
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true ORDER BY r.totalReviews DESC, r.rating DESC")
    List<Restaurant> findPopularRestaurants();

    // Check if restaurant exists by name
    boolean existsByName(String name);

    // Check if restaurant exists by phone number
    boolean existsByPhoneNumber(String phoneNumber);

    // Find restaurants by location (you can expand this with coordinates later)
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.address) LIKE LOWER(CONCAT('%', :location, '%')) AND r.isActive = true")
    List<Restaurant> findByLocation(@Param("location") String location);
}
