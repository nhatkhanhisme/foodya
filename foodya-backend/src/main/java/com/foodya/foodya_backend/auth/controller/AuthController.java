package com.foodya.foodya_backend.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodya.foodya_backend.auth.dto.JwtAuthResponse;
import com.foodya.foodya_backend.auth.dto.LoginRequest;
import com.foodya.foodya_backend.auth.dto.RegisterRequest;
import com.foodya.foodya_backend.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")

public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }


  // CRUD endpoints for authentication will go here
  // POST /api/v1/auth/register
  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
    authService.registerUser(request);
    return new  ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
  }

  // POST /api/vi/auth/login
  @PostMapping("/login")
  public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest request) {
    JwtAuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }
}
