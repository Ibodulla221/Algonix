package com.code.algonix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Algonix API")
                        .version("2.0")
                        .description("""
                                # Algonix - Coding Platform API
                                
                                LeetCode kabi dasturlash muammolarini yechish platformasi.
                                
                                ## Xususiyatlar:
                                - 🔐 JWT Authentication
                                - 📝 Problem CRUD operations
                                - 🚀 Code execution (15+ languages)
                                - 📊 Submission tracking
                                - 👤 User profile management
                                
                                ## Qo'llab-quvvatlanadigan tillar:
                                Java, Python, C++, C, JavaScript, TypeScript, Go, Kotlin, Swift, Rust, Ruby, PHP, Dart, Scala, C#
                                
                                ## Test uchun:
                                - Admin: `username: admin, password: admin123`
                                - User: `username: testuser, password: test123`
                                """))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token kiriting (Bearer prefiksiz)")
                        ));
    }
}
