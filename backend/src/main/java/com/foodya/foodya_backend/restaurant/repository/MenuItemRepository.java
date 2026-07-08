package com.foodya.foodya_backend.restaurant.repository;

import com.foodya.foodya_backend.restaurant.model.MenuItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

  List<MenuItem> findByRestaurantId(UUID restaurantId);

  List<MenuItem> findByRestaurantIdAndIsActiveTrue(UUID restaurantId);

  List<MenuItem> findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(UUID restaurantId);

  Page<MenuItem> findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(UUID restaurantId, Pageable pageable);

  // Category-based queries — using FK (category_id)
  List<MenuItem> findByRestaurantIdAndCategory_Id(UUID restaurantId, UUID categoryId);

  List<MenuItem> findByRestaurantIdAndCategory_IdAndIsActiveTrue(UUID restaurantId, UUID categoryId);

  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND m.isActive = true")
  List<MenuItem> searchByRestaurantAndName(@Param("restaurantId") UUID restaurantId, @Param("keyword") String keyword);

  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isActive = true ORDER BY m.orderCount DESC")
  Page<MenuItem> findPopularItemsByRestaurant(@Param("restaurantId") UUID restaurantId, Pageable pageable);

  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
      "AND (:vegetarian IS NULL OR m.isVegetarian = :vegetarian) " +
      "AND (:vegan IS NULL OR m.isVegan = :vegan) " +
      "AND (:glutenFree IS NULL OR m.isGlutenFree = :glutenFree) " +
      "AND m.isActive = true")
  List<MenuItem> findByDietaryPreferences(
      @Param("restaurantId") UUID restaurantId,
      @Param("vegetarian") Boolean vegetarian,
      @Param("vegan") Boolean vegan,
      @Param("glutenFree") Boolean glutenFree);

  boolean existsByNameAndRestaurantId(String name, UUID restaurantId);
}
