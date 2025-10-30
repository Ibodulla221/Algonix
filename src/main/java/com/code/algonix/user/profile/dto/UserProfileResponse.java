package com.code.algonix.user.profile.dto;

import com.code.algonix.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
