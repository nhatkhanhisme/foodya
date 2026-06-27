package com.foodya.foodya_backend.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foodya.foodya_backend.auth.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtAuthenticationEntryPoint entryPoint;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractToken(request);

    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      if (!jwtService.validateToken(token)) {
        rejectWithUnauthorized(request, response, new BadCredentialsException("Invalid or expired token"));
        return;
      }

      String username = jwtService.extractUsername(token);
      if (!StringUtils.hasText(username)) {
        rejectWithUnauthorized(request, response, new BadCredentialsException("Token missing subject claim"));
        return;
      }

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

      if (!userDetails.isEnabled()) {
        rejectWithUnauthorized(request, response, new DisabledException("Account is disabled"));
        return;
      }

      if (!userDetails.isAccountNonLocked()) {
        rejectWithUnauthorized(request, response, new LockedException("Account is locked"));
        return;
      }

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.debug("Authenticated user '{}' for {}", username, request.getRequestURI());
      filterChain.doFilter(request, response);

    } catch (AuthenticationException ex) {
      rejectWithUnauthorized(request, response, ex);
    } catch (Exception ex) {
      log.error("Unexpected error during JWT authentication for {}", request.getRequestURI(), ex);
      rejectWithUnauthorized(request, response, new BadCredentialsException("Authentication failed"));
    }
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
      return header.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  private void rejectWithUnauthorized(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException ex) throws IOException {
    SecurityContextHolder.clearContext();
    entryPoint.commence(request, response, ex);
  }
}
