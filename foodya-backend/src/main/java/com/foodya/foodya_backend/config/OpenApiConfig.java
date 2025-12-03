package com.foodya.foodya_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    // 1. Thông tin chung của API (Metadata)
    info = @Info(
        title = "Todo App API - Spring Boot 3 & JWT",
        description = "API cho dự án Quản lý công việc cơ bản, bao gồm Authentication (Đăng nhập/Đăng ký) và CRUD.",
        version = "v1.0",
        contact = @Contact(
            name = "Ho Nhat Khanh",
            email = "hnk.uit.k18@gmail.com"
        )
    ),
    // 2. Yêu cầu Bảo mật mặc định cho TẤT CẢ endpoint
    security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
    // 3. Định nghĩa cơ chế Bảo mật JWT/Bearer
    name = "BearerAuth", // Phải trùng với name trong @SecurityRequirement
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Authorization header sử dụng Bearer scheme."
)
public class OpenApiConfig {
}
