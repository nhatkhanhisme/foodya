package com.foodya.foodya_backend.order. repository;

import com.foodya.foodya_backend.order. model.Order;
import com.foodya.foodya_backend.order.model.OrderStatus;
import org.springframework.data. jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data. repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // ========== BASIC QUERIES ==========

    /**
     * Tìm orders của customer (newest first)
     */
    @Query("SELECT o FROM Order o WHERE o.customer. id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findByCustomer_Id(@Param("customerId") UUID customerId);

    /**
     * Tìm orders của restaurant (newest first)
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.orderDate DESC")
    List<Order> findByRestaurant_Id(@Param("restaurantId") UUID restaurantId);

    /**
     * Tìm orders theo status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Tìm orders của customer theo status
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status ORDER BY o.orderDate DESC")
    List<Order> findByCustomer_IdAndStatus(
            @Param("customerId") UUID customerId,
            @Param("status") OrderStatus status
    );

    /**
     * Tìm orders của restaurant theo status
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status ORDER BY o.orderDate DESC")
    List<Order> findByRestaurant_IdAndStatus(
            @Param("restaurantId") UUID restaurantId,
            @Param("status") OrderStatus status
    );
}
