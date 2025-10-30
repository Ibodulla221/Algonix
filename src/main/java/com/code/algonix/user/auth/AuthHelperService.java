package com.code.algonix.user.auth;

import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthHelperService {

    private final UserRepository userRepository;

    public UserEntity getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

