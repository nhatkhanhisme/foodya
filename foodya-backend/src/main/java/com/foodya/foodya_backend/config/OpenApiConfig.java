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
        title = "Foodya API",
        description = "API cho dự án app đặt món ăn Foodya.",
        version = "v1.0",
        contact = @Contact(
            name = "Ho Nhat Khanh & Nguyen Quoc Hai",
            email = "hnk.uit.k18@gmail.com"
        )
    ),
    // 2. Yêu cầu Bảo mật mặc định cho TẤT CẢ endpoint
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    // 3. Định nghĩa cơ chế Bảo mật JWT/Bearer
    name = "bearerAuth", // Phải trùng với name trong @SecurityRequirement
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Authorization header sử dụng Bearer scheme."
)
public class OpenApiConfig {
}
