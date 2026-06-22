package com.foodya.foodya_backend.order.repository;

import com.foodya.foodya_backend.order.model.OrderItem;
import org.springframework. data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data. repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    // Find all items in an order
    List<OrderItem> findByOrder_Id(UUID orderId);

    // Find all orders containing a specific menu item
    List<OrderItem> findByMenuItem_Id(UUID menuItemId); 
    // Count total quantity sold for a menu item
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.menuItem. id = :menuItemId")
    Long countTotalQuantitySoldByMenuItemId(@Param("menuItemId") UUID menuItemId);

    // Get total revenue for a menu item
    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.menuItem.id = : menuItemId")
    Double sumRevenueByMenuItemId(@Param("menuItemId") UUID menuItemId);
}
