package com.foodya.foodya_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscore and hyphen")
  @Schema(description = "Unique username for the account", example = "nguyenvana")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Schema(description = "User's email address", example = "nguyenvana@example.com")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
  @Schema(description = "Strong password", example = "SecurePass123!")
  private String password;

  @NotBlank(message = "Full name is required")
  @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
  @Schema(description = "User's full name", example = "Nguyen Van A")
  private String fullName;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid")
  @Schema(description = "User's phone number in international format", example = "+84987654321")
  private String phoneNumber;

  @Schema(description = "User role (default: CUSTOMER)", example = "CUSTOMER", allowableValues = { "CUSTOMER",
      "MERCHANT", "DELIVERY", "ADMIN" })
  @Builder.Default
  private String role = "CUSTOMER";

}
