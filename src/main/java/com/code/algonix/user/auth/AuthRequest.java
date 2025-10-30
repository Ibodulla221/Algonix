package com.code.algonix.user.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
