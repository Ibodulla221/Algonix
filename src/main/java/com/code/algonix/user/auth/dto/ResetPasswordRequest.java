package com.code.algonix.user.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String confirmPassword;

}
