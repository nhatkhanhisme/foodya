package com.foodya.foodya_backend.order.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.order.dto.OrderResponse;
import com.foodya.foodya_backend.order.model.Order;
import com.foodya.foodya_backend.order.model.OrderStatus;
import com.foodya.foodya_backend.order.repository.OrderRepository;
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
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQueryService {

    private static final EnumSet<OrderStatus> ACTIVE_STATUSES = EnumSet.of(
            OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.READY_FOR_PICKUP, OrderStatus.PICKED_UP);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found with id: " + id));
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
            throw new AppException(ErrorCode.VALIDATION_ERROR, "restaurantId, startDate, endDate are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "endDate must be after startDate");
        }
        Long sum = orderRepository.sumRevenueByRestaurantIdAndDateRange(restaurantId, startDate, endDate);
        return sum == null ? 0L : sum;
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
