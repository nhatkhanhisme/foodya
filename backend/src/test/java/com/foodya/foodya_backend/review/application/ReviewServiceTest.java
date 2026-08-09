package com.foodya.foodya_backend.review.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import com.foodya.foodya_backend.common.exception.ForbiddenException;
import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.common.exception.ReviewAlreadyExistsException;
import com.foodya.foodya_backend.common.exception.ValidationException;
import com.foodya.foodya_backend.identity.application.AuthService;
import com.foodya.foodya_backend.identity.domain.User;
import com.foodya.foodya_backend.ordering.api.dto.OrderResponse;
import com.foodya.foodya_backend.ordering.application.OrderService;
import com.foodya.foodya_backend.ordering.domain.OrderStatus;
import com.foodya.foodya_backend.review.api.dto.ReviewRequest;
import com.foodya.foodya_backend.review.api.dto.ReviewResponse;
import com.foodya.foodya_backend.review.domain.Review;
import com.foodya.foodya_backend.review.domain.event.RestaurantRatingChangedEvent;
import com.foodya.foodya_backend.review.persistence.ReviewRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private AuthService authService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private Authentication authentication;

    private ReviewService reviewService;

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID ORDER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, orderService, authService, eventPublisher);
    }

    private OrderResponse deliveredOrder() {
        OrderResponse order = new OrderResponse();
        order.setId(ORDER_ID);
        order.setCustomerId(CUSTOMER_ID);
        order.setRestaurantId(RESTAURANT_ID);
        order.setStatus(OrderStatus.DELIVERED);
        return order;
    }

    private User customerUser() {
        User user = new User();
        user.setId(CUSTOMER_ID);
        return user;
    }

    @Nested
    class CreateReview {

        @Test
        void savesReviewAndPublishesRecalculatedRating() {
            when(authentication.getName()).thenReturn("nguyenvana");
            when(authService.findByUsername("nguyenvana")).thenReturn(customerUser());
            when(orderService.getOrderById(ORDER_ID)).thenReturn(deliveredOrder());
            when(reviewRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
            when(reviewRepository.averageRatingByRestaurantId(RESTAURANT_ID)).thenReturn(4.5);
            when(reviewRepository.countByRestaurantId(RESTAURANT_ID)).thenReturn(2L);

            ReviewRequest request = ReviewRequest.builder().rating(5).comment("Great!").build();
            ReviewResponse response = reviewService.createReview(authentication, ORDER_ID, request);

            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getRestaurantId()).isEqualTo(RESTAURANT_ID);
            assertThat(response.getRating()).isEqualTo(5);

            ArgumentCaptor<RestaurantRatingChangedEvent> eventCaptor =
                    ArgumentCaptor.forClass(RestaurantRatingChangedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().restaurantId()).isEqualTo(RESTAURANT_ID);
            assertThat(eventCaptor.getValue().newAverageRating()).isEqualTo(4.5);
            assertThat(eventCaptor.getValue().newTotalReviews()).isEqualTo(2);
        }

        @Test
        void rejectsWhenCallerIsNotTheOrderCustomer() {
            when(authentication.getName()).thenReturn("someoneelse");
            User otherUser = new User();
            otherUser.setId(UUID.randomUUID());
            when(authService.findByUsername("someoneelse")).thenReturn(otherUser);
            when(orderService.getOrderById(ORDER_ID)).thenReturn(deliveredOrder());

            ReviewRequest request = ReviewRequest.builder().rating(5).build();

            assertThatThrownBy(() -> reviewService.createReview(authentication, ORDER_ID, request))
                    .isInstanceOf(ForbiddenException.class);

            verify(reviewRepository, never()).save(any());
        }

        @Test
        void rejectsWhenOrderIsNotYetDelivered() {
            when(authentication.getName()).thenReturn("nguyenvana");
            when(authService.findByUsername("nguyenvana")).thenReturn(customerUser());
            OrderResponse notDelivered = deliveredOrder();
            notDelivered.setStatus(OrderStatus.PENDING);
            when(orderService.getOrderById(ORDER_ID)).thenReturn(notDelivered);

            ReviewRequest request = ReviewRequest.builder().rating(5).build();

            assertThatThrownBy(() -> reviewService.createReview(authentication, ORDER_ID, request))
                    .isInstanceOf(ValidationException.class);

            verify(reviewRepository, never()).save(any());
        }

        @Test
        void rejectsSecondReviewForSameOrder() {
            when(authentication.getName()).thenReturn("nguyenvana");
            when(authService.findByUsername("nguyenvana")).thenReturn(customerUser());
            when(orderService.getOrderById(ORDER_ID)).thenReturn(deliveredOrder());
            when(reviewRepository.existsByOrderId(ORDER_ID)).thenReturn(true);

            ReviewRequest request = ReviewRequest.builder().rating(5).build();

            assertThatThrownBy(() -> reviewService.createReview(authentication, ORDER_ID, request))
                    .isInstanceOf(ReviewAlreadyExistsException.class);

            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    class GetReviewsByRestaurant {

        @Test
        void returnsMappedPage() {
            Review review = Review.builder()
                    .id(UUID.randomUUID())
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .restaurantId(RESTAURANT_ID)
                    .rating(4)
                    .build();
            Page<Review> page = new PageImpl<>(List.of(review));
            when(reviewRepository.findByRestaurantId(RESTAURANT_ID, PageRequest.of(0, 20))).thenReturn(page);

            Page<ReviewResponse> result =
                    reviewService.getReviewsByRestaurant(RESTAURANT_ID, PageRequest.of(0, 20));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getRating()).isEqualTo(4);
        }
    }

    @Nested
    class DeleteReview {

        @Test
        void deletesAndPublishesRecalculatedRating() {
            UUID reviewId = UUID.randomUUID();
            Review review = Review.builder()
                    .id(reviewId)
                    .restaurantId(RESTAURANT_ID)
                    .rating(3)
                    .build();
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewRepository.averageRatingByRestaurantId(RESTAURANT_ID)).thenReturn(4.0);
            when(reviewRepository.countByRestaurantId(RESTAURANT_ID)).thenReturn(1L);

            reviewService.deleteReview(reviewId);

            verify(reviewRepository).delete(review);
            ArgumentCaptor<RestaurantRatingChangedEvent> eventCaptor =
                    ArgumentCaptor.forClass(RestaurantRatingChangedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().restaurantId()).isEqualTo(RESTAURANT_ID);
        }

        @Test
        void throwsWhenReviewNotFound() {
            UUID reviewId = UUID.randomUUID();
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
