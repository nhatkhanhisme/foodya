package com.foodya.foodya_backend.catalog.application;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.foodya.foodya_backend.ordering.domain.event.OrderDeliveredEvent;
import com.foodya.foodya_backend.ordering.application.DeliveredOrderSummary;
import com.foodya.foodya_backend.ordering.application.OrderService;
import com.foodya.foodya_backend.common.redis.RedisKeys;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestaurantPopularityService {

  private static final Duration WEEK_BUCKET_TTL = Duration.ofDays(35);
  private static final int BACKFILL_DAYS = 28;

  private final StringRedisTemplate redisTemplate;
  private final OrderService orderService;

  // RestaurantService -> RestaurantPopularityService -> OrderService -> RestaurantService
  // is a real collaboration cycle, not a design mistake: RestaurantService reads
  // popularity for a hot customer-facing endpoint, OrderService needs
  // RestaurantService to place orders, and this class only needs OrderService for
  // the admin-triggered backfill(). @Lazy defers this one edge so Spring doesn't
  // have to fully construct all three at once.
  public RestaurantPopularityService(StringRedisTemplate redisTemplate, @Lazy OrderService orderService) {
    this.redisTemplate = redisTemplate;
    this.orderService = orderService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderDelivered(OrderDeliveredEvent event) {
    recordOrderCompleted(event.restaurantId());
  }

  public void recordOrderCompleted(UUID restaurantId) {
    try {
      String key = RedisKeys.popularRestaurantsWeek(Instant.now());
      redisTemplate.opsForZSet().incrementScore(key, restaurantId.toString(), 1);
      redisTemplate.expire(key, WEEK_BUCKET_TTL);
      log.info("Incremented popularity score for restaurant {} in Redis key {}", restaurantId, key);
    } catch (Exception e) {
      log.warn("Failed to record popularity for restaurant {}: {}", restaurantId, e.getMessage());
    }
  }

  public List<UUID> getTopPopularRestaurants(int limit) {
    try {
      String currentWeekKey = RedisKeys.popularRestaurantsWeeksAgo(0);
      List<String> otherWeekKeys = List.of(
          RedisKeys.popularRestaurantsWeeksAgo(1),
          RedisKeys.popularRestaurantsWeeksAgo(2),
          RedisKeys.popularRestaurantsWeeksAgo(3));

      String aggKey = RedisKeys.topRestaurantCacheKey();

      // Time decay: current week counts full, older weeks progressively less
      redisTemplate.opsForZSet().unionAndStore(
          currentWeekKey,
          otherWeekKeys,
          aggKey,
          Aggregate.SUM,
          Weights.of(1.0, 0.7, 0.4, 0.2));

      redisTemplate.expire(aggKey, Duration.ofMinutes(5));
      Set<String> ids = redisTemplate.opsForZSet().reverseRange(aggKey, 0, limit - 1);

      if (ids == null || ids.isEmpty()) {
        log.info("No popular restaurants found in Redis for the last 4 weeks.");
        return Collections.emptyList();
      }
      return ids.stream().map(UUID::fromString).collect(Collectors.toList());
    } catch (Exception e) {
      log.warn("Failed to read popular restaurants from Redis: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  /**
   * Rebuilds weekly popularity buckets from DELIVERED orders of the last 28 days.
   * Idempotent: buckets in the window are wiped and recounted from the DB, which
   * is the source of truth (live increments come from the same DELIVERED rows).
   */
  public long backfill() {
    Instant since = Instant.now().minus(BACKFILL_DAYS, ChronoUnit.DAYS);
    List<DeliveredOrderSummary> deliveries = orderService.getDeliveredOrderSummariesSince(since);

    // Wipe every bucket the 28-day window can touch (spans up to 5 ISO weeks)
    // so a rerun recounts instead of double-counting
    List<String> windowKeys = IntStream.rangeClosed(0, 4)
        .mapToObj(RedisKeys::popularRestaurantsWeeksAgo)
        .toList();
    redisTemplate.delete(windowKeys);

    // Group to one ZINCRBY per (week bucket, restaurant) instead of one per order
    Map<String, Map<String, Long>> countsByWeekKey = deliveries.stream()
        .collect(Collectors.groupingBy(
            // Bucket by when the order was actually delivered — the facade already
            // falls back to orderDate for rows predating deliveredAt stamping
            d -> RedisKeys.popularRestaurantsWeek(d.deliveredAt()),
            Collectors.groupingBy(
                d -> d.restaurantId().toString(),
                Collectors.counting())));

    countsByWeekKey.forEach((key, counts) -> {
      counts.forEach((restaurantId, count) ->
          redisTemplate.opsForZSet().incrementScore(key, restaurantId, count));
      redisTemplate.expire(key, WEEK_BUCKET_TTL);
    });

    log.info("Backfilled popularity from {} delivered orders across {} week buckets",
        deliveries.size(), countsByWeekKey.size());
    return deliveries.size();
  }
}
