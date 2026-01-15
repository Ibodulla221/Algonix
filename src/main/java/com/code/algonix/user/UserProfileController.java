package com.code.algonix.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.code.algonix.user.dto.ChangePasswordRequest;
import com.code.algonix.user.dto.CategoryStatsResponse;
import com.code.algonix.user.dto.DifficultyStatsResponse;
import com.code.algonix.user.dto.UpdateProfileRequest;
import com.code.algonix.user.dto.UserProfileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://10.36.241.244:4200", "http://172.29.0.1:4200"})
@Tag(name = "User Profile", description = "Foydalanuvchi profili API'lari")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(summary = "Joriy foydalanuvchi profilini olish")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        UserProfileResponse profile = userProfileService.getCurrentUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Foydalanuvchi profilini username bo'yicha olish")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable String username,
            Authentication authentication) {

        String currentUsername = authentication != null ? authentication.getName() : null;
        UserProfileResponse profile = userProfileService.getUserProfile(username, currentUsername);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me")
    @Operation(summary = "Profil ma'lumotlarini yangilash")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        UserProfileResponse updatedProfile = userProfileService.updateProfile(username, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Parolni o'zgartirish")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        userProfileService.changePassword(username, request);
        
        return ResponseEntity.ok(Map.of(
            "message", "Password changed successfully"
        ));
    }

    @PostMapping("/me/avatar")
    @Operation(summary = "Avatar rasmini yuklash")
    public ResponseEntity<UserProfileResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        UserProfileResponse updatedProfile = userProfileService.uploadAvatar(username, file);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/me/avatar")
    @Operation(summary = "Avatar rasmini o'chirish")
    public ResponseEntity<Map<String, String>> deleteAvatar(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        userProfileService.deleteAvatar(username);
        
        return ResponseEntity.ok(Map.of(
            "message", "Avatar deleted successfully"
        ));
    }

    @GetMapping("/me/difficulty-stats")
    @Operation(summary = "Foydalanuvchi masala qiyinchilik darajalari statistikasi")
    public ResponseEntity<DifficultyStatsResponse> getDifficultyStats(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        DifficultyStatsResponse stats = userProfileService.getDifficultyStats(username);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/me/category-stats")
    @Operation(summary = "Foydalanuvchi masala category'lari statistikasi")
    public ResponseEntity<CategoryStatsResponse> getCategoryStats(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        CategoryStatsResponse stats = userProfileService.getCategoryStats(username);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/me/daily-problem-stats")
    @Operation(summary = "Foydalanuvchi kunlik masala yechish statistikasi")
    public ResponseEntity<Map<String, Object>> getDailyProblemStats(
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        Map<String, Object> stats = userProfileService.getDailyProblemStats(username, year, month);
        return ResponseEntity.ok(stats);
    }
}