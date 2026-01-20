package com.code.algonix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .openapi("3.0.3")
                .info(new Info()
                        .title("Algonix API")
                        .version("2.0.0")
                        .description("""
                                # Algonix - Coding Platform API
                                
                                LeetCode kabi dasturlash muammolarini yechish platformasi.
                                
                                ## Xususiyatlar:
                                - 🔐 JWT Authentication
                                - 📝 Problem CRUD operations
                                - 🚀 Code execution (18+ languages)
                                - 📊 Submission tracking
                                - 👤 User profile management
                                - 🏆 Contest system
                                - 📈 Leaderboard
                                
                                ## Qo'llab-quvvatlanadigan tillar:
                                JavaScript, Python, Java, C++, C, C#, Go, Rust, PHP, Ruby, Swift, Kotlin, Scala, Perl, R, Dart, TypeScript, Bash
                                
                                ## Test uchun:
                                - Admin: `username: admin, password: admin123`
                                - User: `username: testuser, password: test123`
                                
                                ## Authentication:
                                1. `/api/auth/login` orqali login qiling
                                2. Qaytgan `accessToken`ni "Authorize" tugmasiga kiriting
                                3. Format: `Bearer YOUR_TOKEN_HERE`
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
