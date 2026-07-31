package com.foodya.foodya_backend.shared.security;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class AuthCookieServiceTest {

    private AuthCookieService cookieService;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        cookieService = new AuthCookieService();
        ReflectionTestUtils.setField(cookieService, "secure", true);
        ReflectionTestUtils.setField(cookieService, "sameSite", "Strict");
        ReflectionTestUtils.setField(cookieService, "domain", "");
        response = mock(HttpServletResponse.class);
    }

    private List<String> capturedCookies() {
        var captor = forClass(String.class);
        verify(response, times(2)).addHeader(org.mockito.ArgumentMatchers.eq(HttpHeaders.SET_COOKIE), captor.capture());
        return captor.getAllValues();
    }

    @Test
    void setsRefreshTokenCookieAsHttpOnly() {
        cookieService.setAuthCookies(response, "refresh-token-value", "csrf-token-value", 3_600_000L);

        String refreshCookie = capturedCookies().stream()
                .filter(c -> c.startsWith(AuthCookieService.REFRESH_COOKIE_NAME + "="))
                .findFirst().orElseThrow();

        assertThat(refreshCookie).contains("refresh_token=refresh-token-value");
        assertThat(refreshCookie).contains("HttpOnly");
        assertThat(refreshCookie).contains("Secure");
        assertThat(refreshCookie).contains("SameSite=Strict");
        assertThat(refreshCookie).contains("Path=/api/v1/auth");
        assertThat(refreshCookie).contains("Max-Age=3600");
    }

    @Test
    void setsCsrfCookieAsReadableByJavaScript() {
        cookieService.setAuthCookies(response, "refresh-token-value", "csrf-token-value", 3_600_000L);

        String csrfCookie = capturedCookies().stream()
                .filter(c -> c.startsWith(AuthCookieService.CSRF_COOKIE_NAME + "="))
                .findFirst().orElseThrow();

        assertThat(csrfCookie).contains("XSRF-TOKEN=csrf-token-value");
        assertThat(csrfCookie).doesNotContain("HttpOnly");
    }

    @Test
    void clearAuthCookiesExpiresBothCookiesImmediately() {
        cookieService.clearAuthCookies(response);

        List<String> cookies = capturedCookies();
        assertThat(cookies).allMatch(c -> c.contains("Max-Age=0"));
        assertThat(cookies).anyMatch(c -> c.startsWith(AuthCookieService.REFRESH_COOKIE_NAME + "="));
        assertThat(cookies).anyMatch(c -> c.startsWith(AuthCookieService.CSRF_COOKIE_NAME + "="));
    }

    @Test
    void omitsDomainAttributeWhenNotConfigured() {
        cookieService.setAuthCookies(response, "refresh-token-value", "csrf-token-value", 3_600_000L);

        assertThat(capturedCookies()).noneMatch(c -> c.contains("Domain="));
    }

    @Test
    void includesDomainAttributeWhenConfigured() {
        ReflectionTestUtils.setField(cookieService, "domain", ".foodya.com");
        cookieService.setAuthCookies(response, "refresh-token-value", "csrf-token-value", 3_600_000L);

        assertThat(capturedCookies()).anyMatch(c -> c.contains("Domain=.foodya.com"));
    }

    @Test
    void negativeMaxAgeIsClampedToZero() {
        cookieService.setAuthCookies(response, "refresh-token-value", "csrf-token-value", -5_000L);

        assertThat(capturedCookies()).allMatch(c -> c.contains("Max-Age=0"));
    }
}
