package com.foodya.foodya_backend.order.service;

import com.foodya.foodya_backend.order.dto.OrderItemRequest;
import com.foodya. foodya_backend.order.dto.OrderRequest;
import com. foodya.foodya_backend. order.dto.OrderResponse;
import com. foodya.foodya_backend. order.model.Order;
import com.foodya.foodya_backend.order.model.OrderItem;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order. repository.OrderRepository;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya. foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.repository.MenuItemRepository;
import com.foodya.foodya_backend.restaurant.repository.RestaurantRepository;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;
import com.foodya.foodya_backend.utils.exception.business.BadRequestException;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org. springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java. util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream. Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;
  private final MenuItemRepository menuItemRepository;

  // ========== CREATE ORDER ==========

  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    log.info("Creating order for customer: {}, restaurant: {}",
        request.getCustomerId(), request.getRestaurantId());

    // 1. Validate customer
    User customer = userRepository.findById(request.getCustomerId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer not found with id: " + request.getCustomerId()));

    // 2. Validate restaurant
    Restaurant restaurant = restaurantRepository.findById(request. getRestaurantId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Restaurant not found with id: " + request.getRestaurantId()));

    // 3. Create Order (WITHOUT orderItems first)
    Order order = Order.builder()
        .customer(customer)
        .restaurant(restaurant)
        .status(OrderStatus.PENDING)
        .orderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now())
        .deliveryAddress(request.getDeliveryAddress())
        .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : 0.0)
        .orderNotes(request.getOrderNotes())
        .totalPrice(0.0)
        .totalItems(0)
        .orderItems(new ArrayList<>())  // ← Initialize empty list
        .build();

    // 4. Create OrderItems
    for (OrderItemRequest itemRequest : request.getItems()) {
      MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
          .orElseThrow(() -> new ResourceNotFoundException(
              "Menu item not found with id: " + itemRequest.getMenuItemId()));

      // Validate menu item thuộc restaurant này
      if (! menuItem.getRestaurant().getId().equals(restaurant.getId())) {
        throw new BadRequestException(
            "Menu item " + menuItem.getName() + " does not belong to this restaurant");
      }

      // Validate menu item còn available
      if (! menuItem.getIsAvailable() || ! menuItem.getIsActive()) {
        throw new BadRequestException(
            "Menu item " + menuItem.getName() + " is not available");
      }

      // 🔥 FIX: Tạo OrderItem VÀ manually calculate subtotal
      OrderItem orderItem = OrderItem.builder()
          .order(order)  // ← SET ORDER NGAY
          .menuItem(menuItem)
          .quantity(itemRequest. getQuantity())
          .priceAtPurchase(menuItem.getPrice())
          .build();

      // Calculate subtotal manually (tránh @PrePersist issues)
      orderItem.setSubtotal(orderItem.getQuantity() * orderItem.getPriceAtPurchase());

      // Add to order
      order.getOrderItems().add(orderItem);
    }

    // 5. 🔥 FIX:  Calculate totals BEFORE save
    order.recalculateTotals();

    // 6. Save order (cascade will save orderItems)
    Order savedOrder = orderRepository.save(order);

    log.info("Order created successfully with ID: {}, Total: {}",
        savedOrder.getId(), savedOrder.getTotalPrice());

    return OrderResponse.fromEntity(savedOrder);
  }

  // ========== GET ORDER BY ID ==========

  @Transactional(readOnly = true)
  public OrderResponse getOrderById(@NonNull UUID id) {
    log.info("Fetching order with ID: {}", id);

    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    return OrderResponse.fromEntity(order);
  }

  // ========== GET ORDERS BY CUSTOMER ==========

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrdersByCustomer(@NonNull UUID customerId) {
    log.info("Fetching orders for customer: {}", customerId);

    List<Order> orders = orderRepository.findByCustomer_Id(customerId);

    return orders.stream()
        .map(OrderResponse::fromEntity)
        .collect(Collectors.toList());
  }

  // ========== GET ORDERS BY RESTAURANT ==========

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrdersByRestaurant(@NonNull UUID restaurantId) {
    log.info("Fetching orders for restaurant: {}", restaurantId);

    List<Order> orders = orderRepository. findByRestaurant_Id(restaurantId);

    return orders.stream()
        .map(OrderResponse::fromEntity)
        .collect(Collectors.toList());
  }

  // ========== GET ACTIVE ORDERS ==========

  @Transactional(readOnly = true)
  public List<OrderResponse> getActiveOrdersByCustomer(@NonNull UUID customerId) {
    log.info("Fetching active orders for customer: {}", customerId);

    List<Order> orders = orderRepository.findByCustomer_Id(customerId);

    return orders.stream()
        .filter(order -> order.getStatus() == OrderStatus.PENDING ||
                        order.getStatus() == OrderStatus.PREPARING ||
                        order.getStatus() == OrderStatus.SHIPPING)
        .map(OrderResponse::fromEntity)
        .collect(Collectors.toList());
  }

  // ========== UPDATE ORDER STATUS ==========

  @Transactional
  public OrderResponse updateOrderStatus(@NonNull UUID id, OrderStatus newStatus) {
    log.info("Updating order {} to status: {}", id, newStatus);

    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    order.updateStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);

    log.info("Order status updated successfully");
    return OrderResponse.fromEntity(updatedOrder);
  }

  // ========== CANCEL ORDER ==========

  @Transactional
  public OrderResponse cancelOrder(@NonNull UUID id, String cancelReason) {
    log.info("Cancelling order:  {}", id);

    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    if (! order.isCancellable()) {
      throw new BadRequestException(
          "Order cannot be cancelled in current status: " + order.getStatus());
    }

    order.cancel(cancelReason);
    Order cancelledOrder = orderRepository.save(order);

    log.info("Order cancelled successfully");
    return OrderResponse.fromEntity(cancelledOrder);
  }

  // ========== DELETE ORDER ==========

  @Transactional
  public void deleteOrder(@NonNull UUID id) {
    log.info("Deleting order: {}", id);

    if (! orderRepository.existsById(id)) {
      throw new ResourceNotFoundException("Order not found with id: " + id);
    }

    orderRepository.deleteById(id);
    log.info("Order deleted successfully");
  }
}
