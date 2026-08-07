package com.foodya.foodya_backend.ordering.persistence;

import com.foodya.foodya_backend.ordering.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrder_Id(UUID orderId);

    List<OrderItem> findByMenuItem_Id(UUID menuItemId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.menuItem.id = :menuItemId")
    Long countTotalQuantitySoldByMenuItemId(@Param("menuItemId") UUID menuItemId);

    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.menuItem.id = :menuItemId")
    Double sumRevenueByMenuItemId(@Param("menuItemId") UUID menuItemId);
}
