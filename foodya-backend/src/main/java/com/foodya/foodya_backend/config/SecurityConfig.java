package com.foodya.foodya_backend.config;

import com.foodya.foodya_backend.middleware.JwtAuthenticationFilter;
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
  private final UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // ========== PUBLIC ENDPOINTS ==========

            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/actuator/**").permitAll()

            // ========== MOBILE APP ENDPOINTS ==========

            .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/*/menu-items/**").permitAll()

            // ========== USER ENDPOINTS ==========

            .requestMatchers("/api/v1/users/me").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated()

            // ========== MERCHANT REGISTRATION ENDPOINTS (NEW) ==========

            // Customer can submit registration
            .requestMatchers(HttpMethod.POST, "/api/v1/merchant-registration").hasRole("CUSTOMER")

            // Authenticated users can view their registrations
            .requestMatchers(HttpMethod.GET, "/api/v1/merchant-registration/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/v1/merchant-registration/*").authenticated()

            // Can cancel their own registration
            .requestMatchers(HttpMethod.DELETE, "/api/v1/merchant-registration/*").authenticated()

            // ========== MERCHANT ENDPOINTS ==========

            .requestMatchers("/api/v1/merchant/**").hasAnyRole("MERCHANT", "ADMIN")

            // ========== ADMIN ENDPOINTS ==========

            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

            // ========== DEFAULT ==========

            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
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
