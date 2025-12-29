package com.foodya.foodya_backend.middleware;

import com.foodya.foodya_backend.jwt.JwtService;
import com.foodya.foodya_backend.user.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
  }

  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestPath = request.getRequestURI();
    logger.debug("Processing request: {} {}", request.getMethod(), requestPath);

    String token = getJwtTokenFromRequest(request);

    // If no token, let it pass through (will be handled by SecurityConfig)
    if (!StringUtils.hasText(token)) {
      logger.debug("No JWT token found in request: {}", requestPath);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Validate token
      if (!jwtService.validateToken(token)) {
        logger.warn("Invalid or expired JWT token for request: {}", requestPath);
        writeUnauthorized(response, "Invalid or expired JWT token");
        return;
      }

      // Extract username from token
      String username = jwtService.extractUsername(token);
      if (!StringUtils.hasText(username)) {
        logger.warn("Username is null or empty in token for request: {}", requestPath);
        writeUnauthorized(response, "Invalid JWT token: missing username");
        return;
      }

      logger.debug("Loading user details for username: {}", username);

      // Load user details
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

      // Check if user account is enabled
      if (!userDetails.isEnabled()) {
        logger.warn("User account is disabled: {}", username);
        writeUnauthorized(response, "User account is disabled");
        return;
      }

      // Check if account is locked
      if (!userDetails.isAccountNonLocked()) {
        logger.warn("User account is locked: {}", username);
        writeUnauthorized(response, "User account is locked");
        return;
      }

      // Create authentication token
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Set authentication in context
      SecurityContextHolder.getContext().setAuthentication(authentication);

      logger.info("Successfully authenticated user: {} with roles: {} for request: {}",
          username, userDetails.getAuthorities(), requestPath);
      filterChain.doFilter(request, response);

    } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
      logger.error("User not found: {} for request: {}", ex.getMessage(), requestPath);
      writeUnauthorized(response, "User not found");
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context for request: {}", requestPath, ex);
      writeUnauthorized(response, "Authentication failed: " + ex.getMessage());
    }
  }

  private String getJwtTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    logger.debug("Authorization header: {}", bearerToken != null ? "present" : "missing");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      String token = bearerToken.substring(BEARER_PREFIX.length());
      logger.debug("Extracted JWT token (length: {})", token.length());
      return token;
    }

    if (StringUtils.hasText(bearerToken) && !bearerToken.startsWith(BEARER_PREFIX)) {
      logger.warn("Authorization header does not start with 'Bearer ' prefix");
    }

    return null;
  }
  private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
    if (response.isCommitted()) {
      logger.warn("Response already committed, cannot write unauthorized message");
      return;
    }
    response.resetBuffer();
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    response.flushBuffer();
  }
}
