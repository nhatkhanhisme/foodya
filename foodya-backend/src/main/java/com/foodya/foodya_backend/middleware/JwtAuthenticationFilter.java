package com.foodya.foodya_backend.middleware;

import com.foodya.foodya_backend.jwt.JwtService;
import com.foodya.foodya_backend.user.repository.UserRepository;
import com.foodya.foodya_backend.user.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      // Get JWT token from request
      String token = getJwtTokenFromRequest(request);

      // Validate token
      if (StringUtils.hasText(token) && jwtService.validateToken(token)) {
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
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
    }
    filterChain.doFilter(request, response);
  }

  private String getJwtTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
