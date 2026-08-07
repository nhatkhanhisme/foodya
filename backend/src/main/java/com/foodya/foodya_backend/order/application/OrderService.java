package com.foodya.foodya_backend.order.application;

import com.foodya.foodya_backend.shared.exception.OrderNotCancellableException;
import com.foodya.foodya_backend.shared.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.shared.exception.RestaurantClosedException;
import com.foodya.foodya_backend.shared.exception.RestaurantSuspendedException;
import com.foodya.foodya_backend.shared.exception.ValidationException;
import com.foodya.foodya_backend.order.api.dto.OrderItemRequest;
import com.foodya.foodya_backend.order.api.dto.OrderRequest;
import com.foodya.foodya_backend.order.api.dto.OrderResponse;
import com.foodya.foodya_backend.order.domain.Order;
import com.foodya.foodya_backend.order.domain.OrderItem;
import com.foodya.foodya_backend.order.domain.event.OrderDeliveredEvent;
import com.foodya.foodya_backend.order.domain.OrderStatus;
import com.foodya.foodya_backend.order.persistence.OrderRepository;
import com.foodya.foodya_backend.restaurant.domain.MenuItem;
import com.foodya.foodya_backend.restaurant.domain.Restaurant;
import com.foodya.foodya_backend.restaurant.domain.RestaurantStatus;
import com.foodya.foodya_backend.restaurant.application.MenuItemService;
import com.foodya.foodya_backend.restaurant.application.RestaurantService;
import com.foodya.foodya_backend.auth.domain.User;
import com.foodya.foodya_backend.auth.persistence.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final EnumSet<OrderStatus> ACTIVE_STATUSES = EnumSet.of(
            OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.READY_FOR_PICKUP, OrderStatus.PICKED_UP);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(@NonNull Authentication authentication, @NonNull OrderRequest request) {
        User customer = getCurrentUser(authentication);

        if (request.getRestaurantId() == null) {
            throw new ValidationException( "restaurantId is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException( "items is required");
        }
        if (!request.getItems().stream()
                .allMatch(i -> i != null && i.getMenuItemId() != null
                        && i.getQuantity() != null && i.getQuantity() > 0)) {
            throw new ValidationException( "Each item must have menuItemId and quantity > 0");
        }
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank()) {
            throw new ValidationException( "deliveryAddress is required");
        }

        log.info("Creating order for customer: {}, restaurant: {}", customer.getId(), request.getRestaurantId());

        Restaurant restaurant = restaurantService.findById(request.getRestaurantId());

        // BR-06: orderable only when APPROVED and currently open
        if (restaurant.getStatus() != RestaurantStatus.APPROVED) {
            throw new RestaurantSuspendedException(
                    "Restaurant is not accepting orders (status: " + restaurant.getStatus() + ")");
        }
        if (!Boolean.TRUE.equals(restaurant.getIsOpen())) {
            throw new RestaurantClosedException(
                    "Restaurant is currently closed");
        }

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .status(OrderStatus.PENDING)
                .orderDate(Instant.now())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryFee(0L)
                .orderNotes(request.getOrderNotes())
                .totalPrice(0L)
                .totalItems(0)
                .orderItems(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            UUID menuItemId = itemRequest.getMenuItemId();
            if (menuItemId == null) {
                throw new ValidationException( "Menu item ID cannot be null");
            }
            MenuItem menuItem = menuItemService.findById(menuItemId);

            if (menuItem.getRestaurant() == null || menuItem.getRestaurant().getId() == null) {
                throw new ValidationException(
                        "Menu item has no restaurant mapping: " + menuItem.getId());
            }
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new ValidationException(
                        "Menu item " + menuItem.getName() + " does not belong to this restaurant");
            }
            if (menuItem.getIsAvailable() == null || menuItem.getIsActive() == null
                    || !menuItem.getIsAvailable() || !menuItem.getIsActive()) {
                throw new ValidationException(
                        "Menu item " + menuItem.getName() + " is not available");
            }
            if (menuItem.getPrice() == null) {
                throw new ValidationException(
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

        // BR-07/BR-08: the fee is server-computed from the restaurant's own settings;
        // clients never send money fields
        if (restaurant.getMinimumOrder() != null && order.getSubtotal() < restaurant.getMinimumOrder()) {
            throw new ValidationException(
                    "Order subtotal is below the restaurant's minimum of " + restaurant.getMinimumOrder());
        }
        order.setDeliveryFee(resolveDeliveryFee(restaurant, order.getSubtotal()));
        order.recalculateTotals();

        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }

    private long resolveDeliveryFee(Restaurant restaurant, long subtotal) {
        long fee = restaurant.getDeliveryFee() != null ? restaurant.getDeliveryFee() : 0L;
        Long freeThreshold = restaurant.getFreeDeliveryThreshold();
        if (freeThreshold != null && freeThreshold > 0 && subtotal >= freeThreshold) {
            return 0L;
        }
        return fee;
    }

    @Transactional
    public OrderResponse cancelMyOrder(
            @NonNull Authentication authentication, @NonNull UUID orderId, String cancelReason) {
        User customer = getCurrentUser(authentication);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        if (order.getCustomerId() == null || !order.getCustomerId().equals(customer.getId())) {
            throw new AccessDeniedException("You are not allowed to cancel this order");
        }
        if (!order.isCancellable()) {
            throw new OrderNotCancellableException(
                    "Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.cancel(cancelReason);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderStatus(@NonNull UUID id, OrderStatus newStatus) {
        return updateOrderStatus(id, newStatus, null);
    }

    @Transactional
    public OrderResponse updateOrderStatus(@NonNull UUID id, OrderStatus newStatus, String reason) {
        // UC-R05: a rejection must tell the customer why
        if (newStatus == OrderStatus.REJECTED && (reason == null || reason.isBlank())) {
            throw new ValidationException( "A reason is required when rejecting an order");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( "Order not found with id: " + id));
        order.updateStatus(newStatus);
        if ((newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED) && reason != null) {
            order.setCancelReason(reason);
        }
        Order saved = orderRepository.save(order);
        if (newStatus == OrderStatus.DELIVERED) {
            eventPublisher.publishEvent(new OrderDeliveredEvent(saved.getId(), saved.getRestaurant().getId()));
        }
        return OrderResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(@NonNull Authentication authentication) {
        User customer = getCurrentUser(authentication);
        return orderRepository.findByCustomer_Id(customer.getId()).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyActiveOrders(@NonNull Authentication authentication) {
        User customer = getCurrentUser(authentication);
        return orderRepository.findByCustomer_IdAndStatusIn(customer.getId(), List.copyOf(ACTIVE_STATUSES))
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(@NonNull UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( "Order not found with id: " + id));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRestaurant(@NonNull UUID restaurantId) {
        return orderRepository.findByRestaurant_Id(restaurantId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrders(
            OrderStatus status, UUID restaurantId, UUID customerId, Instant startDate, Instant endDate) {
        return orderRepository.adminSearch(status, restaurantId, customerId, startDate, endDate)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long calculateRevenue(UUID restaurantId, Instant startDate, Instant endDate) {
        if (restaurantId == null || startDate == null || endDate == null) {
            throw new ValidationException( "restaurantId, startDate, endDate are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new ValidationException( "endDate must be after startDate");
        }
        Long sum = orderRepository.sumRevenueByRestaurantIdAndDateRange(restaurantId, startDate, endDate);
        return sum == null ? 0L : sum;
    }

    /**
     * Facade for restaurant popularity backfill: hands back only what's needed
     * to bucket DELIVERED orders, instead of exposing OrderRepository/Order
     * across the module boundary.
     */
    @Transactional(readOnly = true)
    public List<DeliveredOrderSummary> getDeliveredOrderSummariesSince(Instant since) {
        return orderRepository.findByStatusAndDeliveredAfter(OrderStatus.DELIVERED, since).stream()
                .filter(o -> o.getRestaurant() != null && o.getRestaurant().getId() != null)
                .map(o -> new DeliveredOrderSummary(
                        o.getRestaurant().getId(),
                        o.getDeliveredAt() != null ? o.getDeliveredAt() : o.getOrderDate()))
                .collect(Collectors.toList());
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + authentication.getName()));
    }
}
