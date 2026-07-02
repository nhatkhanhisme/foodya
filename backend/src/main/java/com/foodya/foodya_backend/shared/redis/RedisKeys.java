package com.foodya.foodya_backend.shared.redis;

public final class RedisKeys {

    private RedisKeys() {}

    private static final String TOKEN_BLACKLIST   = "auth:blacklist:";
    private static final String RATE_LIMIT_LOGIN  = "rate:login:";

    public static String tokenBlacklist(String token) {
        return TOKEN_BLACKLIST + token;
    }

    public static String rateLoginByIp(String ip) {
        return RATE_LIMIT_LOGIN + ip;
    }
}
