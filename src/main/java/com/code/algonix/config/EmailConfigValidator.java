package com.code.algonix.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "email.validation.enabled", havingValue = "true", matchIfMissing = false)
public class EmailConfigValidator {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @PostConstruct
    public void validateEmailConfiguration() {
        log.info("Email configuration validation started...");

        // Check if credentials are provided
        if (emailUsername == null || emailUsername.isBlank()) {
            log.warn("⚠️ Email username is not configured!");
            return;
        }

        if (emailPassword == null || emailPassword.isBlank() || emailPassword.equals("1234")) {
            log.warn("⚠️ Email password is not configured or using default value!");
            log.warn("⚠️ Gmail App Password kerak. Quyidagi yo'riqnomani bajaring:");
            log.warn("   1. Google Account Settings > Security");
            log.warn("   2. 2-Step Verification yoqing");
            log.warn("   3. App Passwords yarating");
            log.warn("   4. Olingan parolni application.properties ga kiriting");
            return;
        }

        // Test email connection
        try {
            mailSender.createMimeMessage(); // This will test the connection
            log.info("✅ Email configuration is valid!");
            log.info("✅ Email: {}", emailUsername);
        } catch (Exception e) {
            log.error("❌ Email configuration test failed!");
            log.error("❌ Email: {}", emailUsername);
            log.error("❌ Error: {}", e.getMessage());
            log.error("⚠️ Iltimos, email credentials'ni tekshiring:");
            log.error("   - Gmail App Password ishlatilganligini tekshiring");
            log.error("   - 2-Factor Authentication yoqilganligini tekshiring");
            log.error("   - Internet connection'ni tekshiring");
        }
    }
}
