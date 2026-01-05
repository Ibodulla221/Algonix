package com.code.algonix.config;

import com.code.algonix.messages.MessageService;
import com.code.algonix.user.Role;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import com.code.algonix.user.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OAuth2Config {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MessageService messageService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String login = oauth2User.getAttribute("login"); // GitHub username
            
            log.info("OAuth2 login attempt - Provider: {}, Email: {}, Name: {}", provider, email, name);
            
            // Find or create user
            UserEntity user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        UserEntity newUser = UserEntity.builder()
                                .username(login != null ? login : email.split("@")[0])
                                .email(email)
                                .password("") // OAuth users don't have password
                                .role(Role.USER)
                                .build();
                        userRepository.save(newUser);
                        
                        // Welcome message yaratish
                        messageService.createWelcomeMessage(newUser);
                        
                        log.info("New OAuth2 user created: {}", email);
                        return newUser;
                    });
            
            return oauth2User;
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));
            
            // Streak tekshirish va yangilash
            messageService.checkAndUpdateStreak(user);
            
            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            log.info("OAuth2 login successful for user: {}", email);
            
            // Redirect to frontend with tokens
            String redirectUrl = String.format("%s/auth/callback?accessToken=%s&refreshToken=%s",
                    frontendUrl,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
            
            response.sendRedirect(redirectUrl);
        };
    }
}
