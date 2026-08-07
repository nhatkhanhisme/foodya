package com.foodya.foodya_backend.common.utils;

import java.util.UUID;

import org.slf4j.MDC;

public class TraceIdUtil {

  private static final String TRACE_ID_KEY = "traceId";

  // Get or create a trace ID for the current thread/request
  public static String getOrCreateTraceId() {
    String traceId = MDC.get(TRACE_ID_KEY);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
      MDC.put(TRACE_ID_KEY, traceId);
    }
    return traceId;
  }

  // Set trace ID 
  public static void setTraceId(String traceId) {
    MDC.put(TRACE_ID_KEY, traceId);
  }

  // Get the current trace ID or null if not set
  public static String getTraceId() {
    return MDC.get(TRACE_ID_KEY);
  }

  public static void clearTraceId() {
    MDC.remove(TRACE_ID_KEY);
  }
}
