package com.foodya.foodya_backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request to create a new order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

  @NotNull(message = "Customer ID is required")
  @Schema(description = "Customer ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
  private UUID customerId;
  @NotNull(message = "Restaurant ID is required")
  @Schema(description = "Restaurant ID", example = "8f8e8334-9347-4933-9333-875865538050")
  private UUID restaurantId;

  @NotNull(message = "Order items are required")
  @NotEmpty(message = "Order must contain at least one item")
  @Valid
  @Schema(description = "List of items in the order")
  private List<OrderItemRequest> items = new ArrayList<>();

  @NotBlank(message = "Delivery address is required")
  @Size(max = 500, message = "Delivery address must not exceed 500 characters")
  @Schema(description = "Delivery address", example = "KTX Khu B ĐHQG TP. HCM")
  private String deliveryAddress;

  @DecimalMin(value = "0.0", message = "Delivery fee must be non-negative")
  @Schema(description = "Delivery fee", example = "15000.0")
  private Double deliveryFee;

  @Size(max = 1000, message = "Order notes must not exceed 1000 characters")
  @Schema(description = "Special notes for the order", example = "Giao trước 12h")
  private String orderNotes;

  @Schema(description = "Order date (optional, defaults to current time)")
  private LocalDateTime orderDate;

}
