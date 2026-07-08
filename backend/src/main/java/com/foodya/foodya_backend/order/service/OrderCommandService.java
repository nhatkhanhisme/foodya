package com.foodya.foodya_backend.order.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.order.dto.OrderItemRequest;
import com.foodya.foodya_backend.order.dto.OrderRequest;
import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.model.Order;
import com.foodya.foodya_backend.order.model.OrderItem;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order.repository.OrderRepository;
import com.foodya.foodya_backend.restaurant.model.MenuItem;
import com.foodya.foodya_backend.restaurant.model.Restaurant;
import com.foodya.foodya_backend.restaurant.service.MenuItemCommandService;
import com.foodya.foodya_backend.restaurant.service.RestaurantCommandService;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantCommandService restaurantCommandService;
    private final MenuItemCommandService menuItemCommandService;

    @Transactional
    public OrderResponse createOrder(@NonNull Authentication authentication, @NonNull OrderRequest request) {
        User customer = getCurrentUser(authentication);

        if (request.getRestaurantId() == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "restaurantId is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "items is required");
        }
        if (!request.getItems().stream()
                .allMatch(i -> i != null && i.getMenuItemId() != null
                        && i.getQuantity() != null && i.getQuantity() > 0)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Each item must have menuItemId and quantity > 0");
        }
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "deliveryAddress is required");
        }

        log.info("Creating order for customer: {}, restaurant: {}", customer.getId(), request.getRestaurantId());

        Restaurant restaurant = restaurantCommandService.findById(request.getRestaurantId());

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .status(OrderStatus.PENDING)
                .orderDate(request.getOrderDate() != null ? request.getOrderDate() : Instant.now())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : 0L)
                .orderNotes(request.getOrderNotes())
                .totalPrice(0L)
                .totalItems(0)
                .orderItems(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            UUID menuItemId = itemRequest.getMenuItemId();
            if (menuItemId == null) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "Menu item ID cannot be null");
            }
            MenuItem menuItem = menuItemCommandService.findById(menuItemId);

            if (menuItem.getRestaurant() == null || menuItem.getRestaurant().getId() == null) {
                throw new AppException(ErrorCode.VALIDATION_ERROR,
                        "Menu item has no restaurant mapping: " + menuItem.getId());
            }
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new AppException(ErrorCode.VALIDATION_ERROR,
                        "Menu item " + menuItem.getName() + " does not belong to this restaurant");
            }
            if (menuItem.getIsAvailable() == null || menuItem.getIsActive() == null
                    || !menuItem.getIsAvailable() || !menuItem.getIsActive()) {
                throw new AppException(ErrorCode.VALIDATION_ERROR,
                        "Menu item " + menuItem.getName() + " is not available");
            }
            if (menuItem.getPrice() == null) {
                throw new AppException(ErrorCode.VALIDATION_ERROR,
                        "Menu item " + menuItem.getName() + " has no price");
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemRequest.getQuantity())
                    .itemNameSnapshot(menuItem.getName())
                    .priceAtPurchase(menuItem.getPrice())
                    .build();
            orderItem.setSubtotal(orderItem.getQuantity() * orderItem.getPriceAtPurchase());
            order.getOrderItems().add(orderItem);
        }

        order.recalculateTotals();
        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }

    @Transactional
    public OrderResponse cancelMyOrder(
            @NonNull Authentication authentication, @NonNull UUID orderId, String cancelReason) {
        User customer = getCurrentUser(authentication);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Order not found with id: " + orderId));

        if (order.getCustomerId() == null || !order.getCustomerId().equals(customer.getId())) {
            throw new AccessDeniedException("You are not allowed to cancel this order");
        }
        if (!order.isCancellable()) {
            throw new AppException(ErrorCode.ORDER_NOT_CANCELLABLE,
                    "Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.cancel(cancelReason);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderStatus(@NonNull UUID id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found with id: " + id));
        order.updateStatus(newStatus);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(@NonNull UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                        "User not found: " + authentication.getName()));
    }
}
