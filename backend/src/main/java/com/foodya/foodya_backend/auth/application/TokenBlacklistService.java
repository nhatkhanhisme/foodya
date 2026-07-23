package com.foodya.foodya_backend.auth.application;

import com.foodya.foodya_backend.shared.redis.RedisKeys;
import com.foodya.foodya_backend.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtService jwtService;

    public void revoke(String token) {
        long ttlMs = jwtService.extractExpirationTime(token) - System.currentTimeMillis();
        if (ttlMs > 0) {
            stringRedisTemplate.opsForValue().set(
                    RedisKeys.tokenBlacklist(token),
                    "1",
                    Duration.ofMillis(ttlMs));
        }
    }

    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisKeys.tokenBlacklist(token)));
    }
}
