package com.foodya.foodya_backend.shared.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Issues/clears the two cookies the web frontend relies on: the HttpOnly
 * refresh-token cookie (invisible to JS, immune to XSS token theft) and the
 * companion XSRF-TOKEN cookie (deliberately readable by JS — the frontend
 * echoes it back in the X-XSRF-TOKEN header on refresh/logout so the backend
 * can tell a same-site request from a forged cross-site one).
 */
@Service
public class AuthCookieService {

  public static final String REFRESH_COOKIE_NAME = "refresh_token";
  public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
  private static final String AUTH_PATH = "/api/v1/auth";

  @Value("${app.cookie.secure}")
  private boolean secure;

  @Value("${app.cookie.same-site}")
  private String sameSite;

  @Value("${app.cookie.domain:}")
  private String domain;

  public void setAuthCookies(HttpServletResponse response, String refreshToken, String csrfToken, long maxAgeMs) {
    Duration maxAge = Duration.ofMillis(Math.max(maxAgeMs, 0));
    addCookie(response, REFRESH_COOKIE_NAME, refreshToken, true, maxAge);
    // Not HttpOnly on purpose: the frontend must be able to read this one
    addCookie(response, CSRF_COOKIE_NAME, csrfToken, false, maxAge);
  }

  public void clearAuthCookies(HttpServletResponse response) {
    addCookie(response, REFRESH_COOKIE_NAME, "", true, Duration.ZERO);
    addCookie(response, CSRF_COOKIE_NAME, "", false, Duration.ZERO);
  }

  private void addCookie(HttpServletResponse response, String name, String value, boolean httpOnly, Duration maxAge) {
    ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
        .httpOnly(httpOnly)
        .secure(secure)
        .sameSite(sameSite)
        .path(AUTH_PATH)
        .maxAge(maxAge);
    if (StringUtils.hasText(domain)) {
      builder.domain(domain);
    }
    response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
  }
}
