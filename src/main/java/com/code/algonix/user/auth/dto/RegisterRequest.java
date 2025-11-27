package com.code.algonix.user.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    
    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    @Size(min = 3, max = 20, message = "Username 3-20 ta belgi orasida bo'lishi kerak")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username faqat harf, raqam va _ belgisidan iborat bo'lishi kerak")
    private String username;
    
    @NotBlank(message = "Email bo'sh bo'lmasligi kerak")
    @Email(message = "Email formati noto'g'ri")
    private String email;
    
    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    @Size(min = 6, max = 100, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak")
    private String password;
}
