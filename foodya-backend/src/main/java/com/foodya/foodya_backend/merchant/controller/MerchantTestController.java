package com.foodya.foodya_backend.merchant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/merchant/test")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
public class MerchantTestController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", authentication != null);

        if (authentication != null) {
            result.put("username", authentication.getName());
            result.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            result.put("principal", authentication.getPrincipal().getClass().getSimpleName());
            result.put("authenticated", authentication.isAuthenticated());
        } else {
            result.put("error", "No authentication found in SecurityContext");
        }

        log.info("Test auth endpoint called. Result: {}", result);
        return ResponseEntity.ok(result);
    }
}

