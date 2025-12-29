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

  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = getJwtTokenFromRequest(request);

    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      logger.debug("No JWT token found in request");
      return;
    }

    try {
      if (!jwtService.validateToken(token)) {
        filterChain.doFilter(request, response);
        logger.debug("Expired or Invalid JWT token");
        return;
      }
        // extract username from token
        String username = jwtService.extractUsername(token);

        // Load user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Create authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Set authentication for user: " + username);
        filterChain.doFilter(request, response);

    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
      writeUnauthorized(response, "Invalid or expired JWT token");
    }
  }

  private String getJwtTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
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
