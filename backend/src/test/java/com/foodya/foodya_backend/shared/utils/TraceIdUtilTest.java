package com.foodya.foodya_backend.shared.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TraceIdUtilTest {

    @BeforeEach
    void setUp() {
      MDC.clear();
    }

    @AfterEach
    void tearDown() {
      MDC.clear();
    }

    @Test
    void getOrCreateTraceId_shouldCreateNew_whenNoneExists() {
        // Act
        String traceId = TraceIdUtil.getOrCreateTraceId();

        // Check not null and valid UUID format
        assertNotNull(traceId);
        assertDoesNotThrow(() -> UUID.fromString(traceId));
    }

    @Test
    void getOrCreateTraceId_shouldReturnSame_whenCalledTwice() {
        String firstCall = TraceIdUtil.getOrCreateTraceId();
        String secondCall = TraceIdUtil.getOrCreateTraceId();
        assertEquals(firstCall, secondCall);
    }

    @Test
    void setTraceId_thenGetTraceId_shouldReturnSameValue() {
        String expectedTraceId = "test-trace-id";
        TraceIdUtil.setTraceId(expectedTraceId);
        String actualTraceId = TraceIdUtil.getTraceId();
        assertEquals(expectedTraceId, actualTraceId);
    }

    @Test
    void getTraceId_shouldReturnNull_whenNotSet() {
        String traceId = TraceIdUtil.getTraceId();
        assertNull(traceId);
    }

    @Test
    void clearTraceId_shouldRemoveValue() {
        // set a trace ID first, then clear it and check that it's null
        TraceIdUtil.setTraceId("test-trace-id");
        TraceIdUtil.clearTraceId();
        String traceId = TraceIdUtil.getTraceId();
        assertNull(traceId);
    }
}