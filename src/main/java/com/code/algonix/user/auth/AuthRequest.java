package com.code.algonix.user.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    
    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    private String username;
    
    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    private String password;
}
