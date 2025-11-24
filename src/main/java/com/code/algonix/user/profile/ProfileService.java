package com.code.algonix.user.profile;


import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import com.code.algonix.user.auth.AuthHelperService;
import com.code.algonix.user.jwt.JwtService;
import com.code.algonix.user.profile.dto.ChangePasswordRequest;
import com.code.algonix.user.profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHelperService authHelperService;

    public ResponseEntity<?> changePassword( Principal principal, ChangePasswordRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Eski parol noto‘g‘ri");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli yangilandi");
    }

    public UserProfileResponse me(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        
        UserProfileResponse.Statistics stats = null;
        if (user.getStatistics() != null) {
            stats = UserProfileResponse.Statistics.builder()
                    .totalSolved(user.getStatistics().getTotalSolved())
                    .easySolved(user.getStatistics().getEasySolved())
                    .mediumSolved(user.getStatistics().getMediumSolved())
                    .hardSolved(user.getStatistics().getHardSolved())
                    .acceptanceRate(user.getStatistics().getAcceptanceRate())
                    .ranking(user.getStatistics().getRanking())
                    .reputation(user.getStatistics().getReputation())
                    .streakDays(user.getStatistics().getStreakDays())
                    .build();
        }
        
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .statistics(stats)
                .build();
    }
}
