package com.foodya.foodya_backend.restaurant.repository;

import com.foodya.foodya_backend.restaurant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Lấy tất cả categories của restaurant (Owner View)
     * Bao gồm tất cả categories để quản lý
     */
    @Query("SELECT c FROM Category c WHERE c.restaurant.id = :restaurantId ORDER BY c.name ASC")
    List<Category> findAllByRestaurantId(@Param("restaurantId") UUID restaurantId);

    /**
     * Lấy categories public của restaurant (Customer View)
     * Chỉ lấy categories active (nếu có field is_active trong tương lai)
     */
    @Query("SELECT c FROM Category c WHERE c.restaurant.id = :restaurantId ORDER BY c.name ASC")
    List<Category> findPublicCategoriesByRestaurantId(@Param("restaurantId") UUID restaurantId);

    /**
     * Kiểm tra category name đã tồn tại trong restaurant chưa
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.restaurant.id = :restaurantId AND LOWER(c.name) = LOWER(:name)")
    boolean existsByNameAndRestaurantId(@Param("name") String name, @Param("restaurantId") UUID restaurantId);

    /**
     * Tìm category by name và restaurant
     */
    @Query("SELECT c FROM Category c WHERE c.restaurant.id = :restaurantId AND LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameAndRestaurantId(@Param("name") String name, @Param("restaurantId") UUID restaurantId);
}

