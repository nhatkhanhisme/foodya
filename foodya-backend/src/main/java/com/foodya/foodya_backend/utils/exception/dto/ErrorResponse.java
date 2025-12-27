package com.foodya.foodya_backend.utils.exception.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response structure")
public class ErrorResponse {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @Schema(example = "2025-01-01T10:15:30")
  private LocalDateTime timestamp;

  @Schema(example = "400")
  private int status;
  @Schema(example = "Bad Request")
  private String error;

  @Schema(example = "Validation failed")
  private String message;
  @Schema(example = "/api/v1/auth/login")
  private String path;

  // Additional fields can be added as needed
  @Schema(description = "Validation errors (if any)")
  private List<ValidationError> validationErrors;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ValidationError {
    @Schema(example = "email")
    private String field;
    @Schema(example = "Email must be valid")
    private String message;
  }

}
