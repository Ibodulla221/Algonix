package com.code.algonix.user.auth;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.user.auth.dto.ForgotPasswordRequest;
import com.code.algonix.user.auth.dto.RegisterRequest;
import com.code.algonix.user.auth.dto.ResetPasswordRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Foydalanuvchi autentifikatsiya API'lari")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Yangi foydalanuvchi ro'yxatdan o'tkazish")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Username va parol bilan login qilish")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Access token'ni yangilash")
    public AuthResponse refresh(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Parolni unutgan foydalanuvchi uchun email yuborish")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Parolni tiklash")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(token, request);
    }

    @GetMapping("/oauth2/github")
    @Operation(
        summary = "GitHub orqali login qilish",
        description = "GitHub OAuth2 login sahifasiga yo'naltiradi. Browser'da ochish kerak."
    )
    public void loginWithGitHub(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/github");
    }

    @GetMapping("/oauth2/google")
    @Operation(
        summary = "Google orqali login qilish",
        description = "Google OAuth2 login sahifasiga yo'naltiradi. Browser'da ochish kerak."
    )
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}
