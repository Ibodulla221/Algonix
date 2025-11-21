package com.code.algonix.user.profile;

import com.code.algonix.user.profile.dto.ChangePasswordRequest;
import com.code.algonix.user.profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileService.changePassword(principal, request));
    }

    @GetMapping("/me")
    public UserProfileResponse me(Principal principal) {
        return profileService.me(principal);
    }
}
