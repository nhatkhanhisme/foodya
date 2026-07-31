package com.foodya.foodya_backend.shared.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodya.foodya_backend.shared.utils.IpUtil;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private IpUtil ipUtil;
    @Mock
    private FilterChain filterChain;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        filter = new RateLimitFilter(stringRedisTemplate, objectMapper, ipUtil);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(ipUtil.extractClientIp(any())).thenReturn("203.0.113.5");
    }

    @Test
    void passesThroughUnrelatedPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/restaurants");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    void passesThroughWrongMethodOnAGuardedPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    void allowsRequestsUnderTheLimitAndSetsExpiryOnFirstAttempt() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(1L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(stringRedisTemplate).expire(anyString(), eq(Duration.ofMinutes(15)));
    }

    @Test
    void doesNotResetExpiryAfterFirstIncrement() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(5L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(stringRedisTemplate, never()).expire(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void blocksLoginAfterTenAttempts() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(11L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("RATE_LIMIT_EXCEEDED");
    }

    @Test
    void allowsTheTenthAttemptButBlocksTheEleventh() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(10L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(429);
    }

    @Test
    void blocksRegisterAfterFiveAttempts() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(6L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/register");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void blocksChangePasswordAfterFiveAttempts() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(6L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/change-password");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void blocksRefreshAfterThirtyAttempts() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(31L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/refresh");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void bucketsAreIndependentPerEndpoint() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(1L);

        filter.doFilterInternal(new MockHttpServletRequest("POST", "/api/v1/auth/login"),
                new MockHttpServletResponse(), filterChain);
        filter.doFilterInternal(new MockHttpServletRequest("POST", "/api/v1/auth/register"),
                new MockHttpServletResponse(), filterChain);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(2)).increment(keyCaptor.capture());
        assertThat(keyCaptor.getAllValues()).containsExactly(
                "rate:login:203.0.113.5", "rate:register:203.0.113.5");
    }
}
