package com.code.algonix.gamification;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final RewardService rewardService;
    private final UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<UserGameStats> getUserGameStats(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGameStats stats = rewardService.getUserGameStats(user);
        return ResponseEntity.ok(stats);
    }
}