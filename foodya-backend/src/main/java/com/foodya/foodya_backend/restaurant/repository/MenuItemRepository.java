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

  // Find by restaurant
  List<MenuItem> findByRestaurantId(UUID restaurantId);

  // Find active menu items by restaurant
  List<MenuItem> findByRestaurantIdAndIsActiveTrue(UUID restaurantId);

  // Find available menu items by restaurant
  List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);

  // Find by category
  List<MenuItem> findByCategory(String category);

  // Find by restaurant and category
  List<MenuItem> findByRestaurantIdAndCategory(UUID restaurantId, String category);

  // Find by restaurant and category (active only)
  List<MenuItem> findByRestaurantIdAndCategoryAndIsActiveTrue(UUID restaurantId, String category);

  // Find by restaurant and category (active and available only)
  List<MenuItem> findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(UUID restaurantId);

  // Search by name
  @Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND m.isActive = true")
  List<MenuItem> searchByName(@Param("keyword") String keyword);

  // Search by restaurant and name
  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND m.isActive = true")
  List<MenuItem> searchByRestaurantAndName(@Param("restaurantId") UUID restaurantId, @Param("keyword") String keyword);

  // Find by price range
  @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice AND m.isActive = true")
  List<MenuItem> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

  // Find by restaurant and price range
  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.price BETWEEN :minPrice AND :maxPrice AND m.isActive = true")
  List<MenuItem> findByRestaurantAndPriceRange(@Param("restaurantId") UUID restaurantId,
      @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

  // Find vegetarian items
  List<MenuItem> findByIsVegetarianTrue();

  // Find vegan items
  List<MenuItem> findByIsVeganTrue();

  // Find gluten-free items
  List<MenuItem> findByIsGlutenFreeTrue();

  // Find spicy items
  List<MenuItem> findByIsSpicyTrue();

  // Find popular items by restaurant
  @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isActive = true ORDER BY m.orderCount DESC")
  Page<MenuItem> findPopularItemsByRestaurant(@Param("restaurantId") UUID restaurantId, Pageable pageable);

  // Find items by dietary preferences
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

  Page<MenuItem> findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue(UUID restaurantId, Pageable pageable);

  // Check if menu item exists by name and restaurant
  boolean existsByNameAndRestaurantId(String name, UUID restaurantId);
}
