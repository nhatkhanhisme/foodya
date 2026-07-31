package com.foodya.foodya_backend.shared.redis;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;

public final class RedisKeys {

    private RedisKeys() {}

    private static final String TOKEN_BLACKLIST   = "auth:blacklist:";
    private static final String RATE_LIMIT        = "rate:";
    private static final String POPULAR_RESTAURANTS = "restaurants:popular:";

    private static final ZoneId POPULARITY_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public static String tokenBlacklist(String token) {
        return TOKEN_BLACKLIST + token;
    }

    public static String rateLimitByIp(String bucket, String ip) {
        return RATE_LIMIT + bucket + ":" + ip;
    }

    public static String popularRestaurantsWeek(Instant instant) {
        return popularRestaurantsWeek(instant.atZone(POPULARITY_ZONE).toLocalDate());
    }

    public static String popularRestaurantsWeeksAgo(int weeksAgo) {
        return popularRestaurantsWeek(LocalDate.now(POPULARITY_ZONE).minusWeeks(weeksAgo));
    }

    private static String popularRestaurantsWeek(LocalDate date) {
        int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = date.get(IsoFields.WEEK_BASED_YEAR);
        return POPULAR_RESTAURANTS + "%d-W%02d".formatted(year, week);
    }

    public static String topRestaurantCacheKey() {
        return POPULAR_RESTAURANTS + "top";
    }
}
