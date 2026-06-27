package com.foodya.foodya_backend.shared.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foodya.foodya_backend.shared.utils.TraceIdUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String incoming = request.getHeader("X-Trace-Id");
    String traceId = (incoming != null && !incoming.isBlank())
        ? incoming
        : UUID.randomUUID().toString();
    TraceIdUtil.setTraceId(traceId);
    response.setHeader("X-Trace-Id", traceId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      TraceIdUtil.clearTraceId();
    }
  }
}
