package com.foodya.foodya_backend.utils.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.foodya.foodya_backend.exception.dto.ErrorResponse;

/**
 * Utility class containing reusable API response annotations for Swagger documentation.
 * These annotations can be used across controllers to maintain consistent error response documentation.
 */
public class ApiResponseExamples {

    // ============= Common Error Responses =============

    /**
     * 400 Bad Request - Validation failed
     * Used when request body fails validation (e.g., missing required fields, invalid format)
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request - Validation failed or invalid input",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Validation Failed",
                value = """
                {
                  "timestamp": "2026-01-01T14:52:00",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Validation failed",
                  "path": "/api/v1/auth/register",
                  "details": {
                    "username": "Username must be between 3 and 50 characters",
                    "email": "Email must be valid",
                    "password": "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
                  }
                }
                """
            )
        )
    )
    public @interface BadRequest {}

    /**
     * 401 Unauthorized - Authentication required or invalid token
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Authentication required or invalid/expired token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "Missing Token",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Full authentication is required to access this resource",
                      "path": "/api/v1/users/profile"
                    }
                    """
                ),
                @ExampleObject(
                    name = "Invalid Token",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "JWT token is expired or invalid",
                      "path": "/api/v1/users/profile"
                    }
                    """
                ),
                @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Invalid username or password",
                      "path": "/api/v1/auth/login"
                    }
                    """
                )
            }
        )
    )
    public @interface Unauthorized {}

    /**
     * 403 Forbidden - Insufficient permissions or account deactivated
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Insufficient permissions or account deactivated",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "Insufficient Permissions",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "You don't have permission to access this resource",
                      "path": "/api/v1/merchant/restaurants/123"
                    }
                    """
                ),
                @ExampleObject(
                    name = "Account Deactivated",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 403,
                      "error": "Forbidden",
                      "message": "Account is deactivated",
                      "path": "/api/v1/auth/login"
                    }
                    """
                )
            }
        )
    )
    public @interface Forbidden {}

    /**
     * 404 Not Found - Resource not found
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "404",
        description = "Not Found - Resource does not exist",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Resource Not Found",
                value = """
                {
                  "timestamp": "2026-01-01T14:52:00",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Restaurant not found with id: 8f8e8334-9347-4933-9333-875865538050",
                  "path": "/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050"
                }
                """
            )
        )
    )
    public @interface NotFound {}

    /**
     * 409 Conflict - Duplicate resource (e.g., username/email already exists)
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "409",
        description = "Conflict - Resource already exists",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "Duplicate Username",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Username already exists",
                      "path": "/api/v1/auth/register"
                    }
                    """
                ),
                @ExampleObject(
                    name = "Duplicate Email",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Email already exists",
                      "path": "/api/v1/auth/register"
                    }
                    """
                ),
                @ExampleObject(
                    name = "Duplicate Phone Number",
                    value = """
                    {
                      "timestamp": "2026-01-01T14:52:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Phone number already exists",
                      "path": "/api/v1/users/profile"
                    }
                    """
                )
            }
        )
    )
    public @interface Conflict {}

    /**
     * 500 Internal Server Error
     */
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Server Error",
                value = """
                {
                  "timestamp": "2026-01-01T14:52:00",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "An unexpected error occurred. Please try again later.",
                  "path": "/api/v1/restaurants"
                }
                """
            )
        )
    )
    public @interface InternalServerError {}

    // ============= Combined Response Groups =============

    /**
     * Standard CRUD Read responses (GET): 200, 404, 500
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @NotFound
    @InternalServerError
    public @interface StandardGetResponses {}

    /**
     * Standard CRUD Create responses (POST): 201, 400, 401, 409, 500
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @BadRequest
    @Unauthorized
    @Conflict
    @InternalServerError
    public @interface StandardCreateResponses {}

    /**
     * Standard CRUD Update responses (PUT/PATCH): 200, 400, 401, 403, 404, 409, 500
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @BadRequest
    @Unauthorized
    @Forbidden
    @NotFound
    @Conflict
    @InternalServerError
    public @interface StandardUpdateResponses {}

    /**
     * Standard CRUD Delete responses (DELETE): 204, 401, 403, 404, 500
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Unauthorized
    @Forbidden
    @NotFound
    @InternalServerError
    public @interface StandardDeleteResponses {}

    /**
     * Public endpoint responses (no auth): 200, 400, 404, 500
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @BadRequest
    @NotFound
    @InternalServerError
    public @interface PublicEndpointResponses {}
}
