package com.code.algonix.user.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.code.algonix.exception.InvalidTokenException;
import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.messages.MessageService;
import com.code.algonix.user.Role;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import com.code.algonix.user.auth.dto.ChangePasswordRequest;
import com.code.algonix.user.auth.dto.ForgotPasswordRequest;
import com.code.algonix.user.auth.dto.RegisterRequest;
import com.code.algonix.user.auth.dto.ResetPasswordRequest;
import com.code.algonix.user.auth.email.EmailService;
import com.code.algonix.user.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailService emailService;
    private final MessageService messageService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // Welcome message yaratish
        messageService.createWelcomeMessage(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        // Streak tekshirish va yangilash
        messageService.checkAndUpdateStreak(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(newAccessToken, refreshToken);
    }


    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email ro'yxatdan o'tmagan");
        }

        UserEntity user = userOptional.get();
        String resetToken = jwtService.generateSimpleToken(user);
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        emailService.sendResetLink(user.getEmail(), resetLink);

        return ResponseEntity.ok("Email yuborildi: " + resetLink);
    }

    public ResponseEntity<?> resetPassword(String token, ResetPasswordRequest request) {
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto'g'ri yoki eskirgan");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Parollar mos emas");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli o'zgartirildi");
    }

    public ResponseEntity<?> changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Tizimga kirilmagan");
        }

        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Hozirgi parol noto'g'ri");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Yangi parollar mos emas");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli o'zgartirildi");
    }
}
