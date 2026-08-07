package com.foodya.foodya_backend.shared.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodya.foodya_backend.shared.exception.RateLimitExceededException;
import com.foodya.foodya_backend.shared.redis.RedisKeys;
import com.foodya.foodya_backend.shared.response.ApiResponse;
import com.foodya.foodya_backend.shared.utils.IpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * IP-scoped rate limiting for the auth endpoints most worth throttling:
 * credential guessing (login, change-password), account-creation spam
 * (register), and refresh-token cycling abuse (refresh). Counters are plain
 * Redis INCR+EXPIRE per (bucket, IP) — no external rate-limit library needed
 * at this volume.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private record Rule(String method, String path, String bucket, int maxAttempts, Duration window) {}

    private static final List<Rule> RULES = List.of(
            new Rule("POST", "/api/v1/auth/login", "login", 10, Duration.ofMinutes(15)),
            new Rule("POST", "/api/v1/auth/register", "register", 5, Duration.ofMinutes(15)),
            new Rule("POST", "/api/v1/auth/refresh", "refresh", 30, Duration.ofMinutes(15)),
            new Rule("POST", "/api/v1/auth/change-password", "change-password", 5, Duration.ofMinutes(15)));

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final IpUtil ipUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Rule rule = matchRule(request);
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = ipUtil.extractClientIp(request);
        String key = RedisKeys.rateLimitByIp(rule.bucket(), ip);

        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            stringRedisTemplate.expire(key, rule.window());
        }

        if (count != null && count > rule.maxAttempts()) {
            log.warn("Rate limit exceeded for {} {} from IP: {}", rule.method(), rule.path(), ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    ApiResponse.error(RateLimitExceededException.CODE, RateLimitExceededException.DEFAULT_MESSAGE));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Rule matchRule(HttpServletRequest request) {
        return RULES.stream()
                .filter(r -> r.method().equalsIgnoreCase(request.getMethod())
                        && r.path().equals(request.getRequestURI()))
                .findFirst()
                .orElse(null);
    }
}
