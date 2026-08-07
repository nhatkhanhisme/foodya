package com.foodya.foodya_backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodya.foodya_backend.shared.response.ApiResponse;
import com.foodya.foodya_backend.shared.exception.AuthInvalidCredentialsException;
import com.foodya.foodya_backend.shared.exception.AuthTokenRevokedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  // Inject the Spring-autoconfigured ObjectMapper (includes JavaTimeModule for
  // Instant serialization in ApiResponse.timestamp)
  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // Distinguish: token present but invalid → AUTH_TOKEN_REVOKED
    //              no token at all → AUTH_INVALID_CREDENTIALS (unauthenticated)
    boolean hasToken = request.getHeader("Authorization") != null;
    String code = hasToken ? AuthTokenRevokedException.CODE : AuthInvalidCredentialsException.CODE;
    String message = hasToken ? AuthTokenRevokedException.DEFAULT_MESSAGE : AuthInvalidCredentialsException.DEFAULT_MESSAGE;

    objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, message));
  }
}
