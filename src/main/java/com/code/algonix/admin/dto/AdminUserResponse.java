package com.code.algonix.admin.dto;

import com.code.algonix.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Integer totalSolved;
    private Integer coins;
    private Integer level;
    private Integer currentStreak;
    private LocalDate lastLoginDate;
}