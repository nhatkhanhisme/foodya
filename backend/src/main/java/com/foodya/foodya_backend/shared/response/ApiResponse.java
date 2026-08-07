package com.foodya.foodya_backend.shared.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodya.foodya_backend.shared.utils.TraceIdUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API Response envelope for all requests")
public class ApiResponse<T> {

  @JsonProperty("success")
  private boolean success;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("data")
  private T data;

  @JsonProperty("meta")
  private PaginationMeta meta;

  @JsonProperty("timestamp")
  private Instant timestamp;

  @JsonProperty("traceId")
  private String traceId;

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(Instant.now())
        .traceId(TraceIdUtil.getTraceId())
        .build();
  }

  public static <T> ApiResponse<T> success(String message, T data, PaginationMeta meta) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .meta(meta)
        .timestamp(Instant.now())
        .traceId(TraceIdUtil.getTraceId())
        .build();
  }

  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .timestamp(Instant.now())
        .traceId(TraceIdUtil.getTraceId())
        .build();
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(code)
        .message(message)
        .timestamp(Instant.now())
        .traceId(TraceIdUtil.getTraceId())
        .build();
  }

  public static <T> ApiResponse<T> error(String code, String message, T data) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(code)
        .message(message)
        .data(data)
        .timestamp(Instant.now())
        .traceId(TraceIdUtil.getTraceId())
        .build();
  }
}
