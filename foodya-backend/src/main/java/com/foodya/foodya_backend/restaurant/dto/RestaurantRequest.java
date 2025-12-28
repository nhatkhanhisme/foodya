package com.foodya.foodya_backend.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a restaurant")
public class RestaurantRequest {

  // ========== BASIC INFORMATION ==========

  @NotBlank(message = "Restaurant name is required")
  @Size(min = 2, max = 200, message = "Restaurant name must be between 2 and 200 characters")
  @Schema(description = "Restaurant name", example = "Phở Hà Nội 24h")
  private String name;

  @NotBlank(message = "Address is required")
  @Size(max = 500, message = "Address must not exceed 500 characters")
  @Schema(description = "Restaurant address", example = "123 Nguyễn Huệ, Quận 1, TP.HCM")
  private String address;

  @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Schema(description = "Restaurant phone number", example = "+84283123456")
  private String phoneNumber;

  @Email(message = "Invalid email format")
  @Schema(description = "Restaurant email", example = "phohanoi@gmail.com")
  private String email;

  @Size(max = 2000, message = "Description must not exceed 2000 characters")
  @Schema(description = "Restaurant description", example = "Phở bò Hà Nội truyền thống, nước dùng hầm xương 12 tiếng")
  private String description;

  @NotBlank(message = "Cuisine type is required")
  @Size(max = 100, message = "Cuisine type must not exceed 100 characters")
  @Schema(description = "Type of cuisine", example = "Vietnamese")
  private String cuisine;

  // ========== MEDIA ==========

  @Schema(description = "Restaurant image URL", example = "https://images.unsplash.com/photo-xxx")
  private String imageUrl;

  @Schema(description = "Cover image URL (large banner)", example = "https://images.unsplash.com/photo-yyy")
  private String coverImageUrl;

  // ========== OPERATING HOURS ==========

  @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format.  Use HH:mm")
  @Schema(description = "Opening time (HH:mm)", example = "08:00")
  private String openingTime;

  @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format.  Use HH:mm")
  @Schema(description = "Closing time (HH:mm)", example = "22:00")
  private String closingTime;

  @Size(max = 200, message = "Opening hours text must not exceed 200 characters")
  @Schema(description = "Opening hours description", example = "Mon-Fri: 08:00-22:00, Sat-Sun: 07:00-23:00")
  private String openingHours;

  // ========== DELIVERY INFORMATION ==========

  @Min(value = 0, message = "Minimum order must be non-negative")
  @Schema(description = "Minimum order value in VND", example = "50000")
  private Double minimumOrder;

  @Min(value = 0, message = "Max delivery distance must be non-negative")
  @Max(value = 100, message = "Max delivery distance must not exceed 100 km")
  @Schema(description = "Maximum delivery distance in km", example = "10")
  private Double maxDeliveryDistance;

  // ========== STATUS ==========

  @Schema(description = "Is restaurant currently open", example = "true")
  private Boolean isOpen;
}
