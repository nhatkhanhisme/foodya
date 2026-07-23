package com.foodya.foodya_backend.restaurant.persistence;

import com.foodya.foodya_backend.restaurant.domain.Restaurant;
import com.foodya.foodya_backend.restaurant.domain.RestaurantStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

  boolean existsByName(String name);
  boolean existsByPhoneNumber(String phoneNumber);

  List<Restaurant> findByOwnerId(UUID ownerId);
  List<Restaurant> findByStatus(RestaurantStatus status);

  Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

  @Query("""
      SELECT r FROM Restaurant r
      WHERE r.status = :status
        AND (:keyword IS NULL
             OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:cuisine IS NULL OR LOWER(r.cuisine) = LOWER(:cuisine))
        AND (:minRating IS NULL OR r.rating >= :minRating)
      """)
  Page<Restaurant> findByFilters(
      @Param("keyword") String keyword,
      @Param("cuisine") String cuisine,
      @Param("minRating") Double minRating,
      @Param("status") RestaurantStatus status,
      Pageable pageable);
}
