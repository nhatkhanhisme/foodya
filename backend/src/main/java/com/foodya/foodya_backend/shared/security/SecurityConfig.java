package com.foodya.foodya_backend.shared.security;

import com.foodya.foodya_backend.shared.filter.TraceIdFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final TraceIdFilter traceIdFilter;
  private final UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Public
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            // Public read
            .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/*/menu-items/**").permitAll()
            // User
            .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated()
            // Merchant registration
            .requestMatchers(HttpMethod.POST, "/api/v1/merchant-registration").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.GET, "/api/v1/merchant-registration/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/v1/merchant-registration/*").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/v1/merchant-registration/*").authenticated()
            // Merchant
            .requestMatchers("/api/v1/merchant/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")
            // Orders
            .requestMatchers("/api/v1/orders/**").authenticated()
            // Admin
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(traceIdFilter, JwtAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
