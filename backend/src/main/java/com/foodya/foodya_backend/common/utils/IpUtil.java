package com.foodya.foodya_backend.common.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IpUtil {

  @Value("${app.trusted-proxy-ips:}")
  private String rawTrustedIps;

  private Set<String> trustedProxyIps;

  @PostConstruct
  void init() {
    trustedProxyIps = Arrays.stream(rawTrustedIps.split(","))
        .map(String::trim)
        .filter(s -> !s.isBlank())
        .collect(Collectors.toSet());

    if (!trustedProxyIps.isEmpty()) {
      log.info("Rate limiter trusts X-Forwarded-For from proxy IPs: {}", trustedProxyIps);
    }
  }

  public String extractClientIp(HttpServletRequest request) {
    String remoteAddr = request.getRemoteAddr();

    if (!trustedProxyIps.isEmpty() && trustedProxyIps.contains(remoteAddr)) {
      String xff = request.getHeader("X-Forwarded-For");
      if (xff != null && !xff.isBlank()) {
        String[] ips = xff.split(",");
        String clientIp = ips[0].trim();
        log.debug("Extracted client IP {} from X-Forwarded-For header", clientIp);
        return clientIp;
      }
    }
    return remoteAddr;
  }
}
