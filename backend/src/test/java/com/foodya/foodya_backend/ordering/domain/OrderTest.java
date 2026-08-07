package com.foodya.foodya_backend.ordering.domain;

import com.foodya.foodya_backend.common.exception.InvalidOrderTransitionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private Order orderIn(OrderStatus status) {
        return Order.builder()
                .status(status)
                .orderItems(new ArrayList<>())
                .deliveryFee(0L)
                .totalPrice(0L)
                .totalItems(0)
                .build();
    }

    private OrderItem item(long price, int quantity) {
        OrderItem i = OrderItem.builder()
                .quantity(quantity)
                .priceAtPurchase(price)
                .build();
        i.setSubtotal(price * quantity);
        return i;
    }

    @Nested
    class StatusTransitions {

        @ParameterizedTest
        @CsvSource({
                "AWAITING_PAYMENT, PENDING",
                "AWAITING_PAYMENT, CANCELLED",
                "PENDING, CONFIRMED",
                "PENDING, REJECTED",
                "PENDING, CANCELLED",
                "CONFIRMED, READY_FOR_PICKUP",
                "CONFIRMED, CANCELLED",
                "READY_FOR_PICKUP, PICKED_UP",
                "READY_FOR_PICKUP, CANCELLED",
                "PICKED_UP, DELIVERED",
                "PICKED_UP, CANCELLED",
        })
        void allowsValidTransitions(OrderStatus from, OrderStatus to) {
            Order order = orderIn(from);
            order.updateStatus(to);
            assertThat(order.getStatus()).isEqualTo(to);
        }

        @ParameterizedTest
        @CsvSource({
                "PENDING, DELIVERED",      // cannot skip the whole flow
                "PENDING, PICKED_UP",
                "CONFIRMED, DELIVERED",
                "CONFIRMED, PENDING",      // no going backwards
                "READY_FOR_PICKUP, CONFIRMED",
                "AWAITING_PAYMENT, CONFIRMED", // restaurant can't act before payment
        })
        void rejectsInvalidTransitions(OrderStatus from, OrderStatus to) {
            Order order = orderIn(from);
            assertThatThrownBy(() -> order.updateStatus(to))
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @ParameterizedTest
        @EnumSource(names = {"DELIVERED", "REJECTED", "CANCELLED"})
        void terminalStatesAllowNoFurtherTransition(OrderStatus terminal) {
            for (OrderStatus target : OrderStatus.values()) {
                Order order = orderIn(terminal);
                assertThatThrownBy(() -> order.updateStatus(target))
                        .as("%s -> %s must be blocked", terminal, target)
                        .isInstanceOf(InvalidOrderTransitionException.class);
            }
        }
    }

    @Nested
    class TransitionTimestamps {

        @Test
        void stampsEachLifecycleMoment() {
            Order order = orderIn(OrderStatus.PENDING);

            order.updateStatus(OrderStatus.CONFIRMED);
            assertThat(order.getConfirmedAt()).isNotNull();

            order.updateStatus(OrderStatus.READY_FOR_PICKUP);
            order.updateStatus(OrderStatus.PICKED_UP);
            assertThat(order.getPickedUpAt()).isNotNull();

            order.updateStatus(OrderStatus.DELIVERED);
            assertThat(order.getDeliveredAt()).isNotNull();
        }

        @Test
        void stampsCancelledAt() {
            Order order = orderIn(OrderStatus.PENDING);
            order.cancel("changed my mind");
            assertThat(order.getCancelledAt()).isNotNull();
            assertThat(order.getCancelReason()).isEqualTo("changed my mind");
            assertThat(order.isCancelled()).isTrue();
        }
    }

    @Nested
    class Cancellability {

        @ParameterizedTest
        @EnumSource(names = {"AWAITING_PAYMENT", "PENDING", "CONFIRMED"})
        void cancellableBeforePreparationFinishes(OrderStatus status) {
            assertThat(orderIn(status).isCancellable()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(names = {"READY_FOR_PICKUP", "PICKED_UP", "DELIVERED", "REJECTED", "CANCELLED"})
        void notCancellableFromReadyForPickupOnwards(OrderStatus status) {
            // UC-C09 alt 1c: once the food is ready, cancellation is blocked
            assertThat(orderIn(status).isCancellable()).isFalse();
        }
    }

    @Nested
    class Totals {

        @Test
        void totalIsSubtotalPlusDeliveryFee() {
            Order order = orderIn(OrderStatus.PENDING);
            order.getOrderItems().add(item(50_000, 2));
            order.getOrderItems().add(item(30_000, 1));
            order.setDeliveryFee(15_000L);

            order.recalculateTotals();

            assertThat(order.getSubtotal()).isEqualTo(130_000L);
            assertThat(order.getTotalPrice()).isEqualTo(145_000L);
            assertThat(order.getTotalItems()).isEqualTo(3);
        }

        @Test
        void nullDeliveryFeeCountsAsZero() {
            Order order = orderIn(OrderStatus.PENDING);
            order.getOrderItems().add(item(20_000, 1));
            order.setDeliveryFee(null);

            order.recalculateTotals();

            assertThat(order.getTotalPrice()).isEqualTo(20_000L);
        }
    }
}
