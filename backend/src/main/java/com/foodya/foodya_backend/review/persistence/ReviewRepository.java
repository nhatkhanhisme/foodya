package com.foodya.foodya_backend.review.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodya.foodya_backend.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

  boolean existsByOrderId(UUID orderId);

  Page<Review> findByRestaurantId(UUID restaurantId, Pageable pageable);

  @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.restaurantId = :restaurantId")
  double averageRatingByRestaurantId(@Param("restaurantId") UUID restaurantId);

  long countByRestaurantId(UUID restaurantId);
}
