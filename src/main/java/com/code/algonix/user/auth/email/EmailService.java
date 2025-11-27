package com.code.algonix.user.auth.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.subject.reset-password:Parolni tiklash}")
    private String resetPasswordSubject;

    public void sendResetLink(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(resetPasswordSubject);
            message.setText("""
                    Assalomu alaykum!
                    
                    Parolni tiklash uchun quyidagi havolani bosing:
                    
                    %s
                    
                    Agar siz bu so'rovni yubormasangiz, bu xabarni e'tiborsiz qoldiring.
                    
                    Havola 1 soat davomida amal qiladi.
                    
                    Hurmat bilan,
                    Algonix jamoasi
                    """.formatted(resetLink));
            message.setFrom(fromEmail);

            mailSender.send(message);
            log.info("✅ Password reset email sent to: {}", toEmail);
        } catch (MailException e) {
            log.error("❌ Failed to send email to: {}", toEmail);
            log.error("Error details: {}", e.getMessage());
            
            String errorMessage = "Email yuborishda xatolik yuz berdi. ";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Authentication failed") || e.getMessage().contains("535")) {
                    errorMessage += "Email yoki parol noto'g'ri. Gmail App Password ishlatilganligini tekshiring.";
                } else if (e.getMessage().contains("Connection")) {
                    errorMessage += "Internet connection'ni tekshiring.";
                } else {
                    errorMessage += "Iltimos, keyinroq urinib ko'ring.";
                }
            }
            
            throw new RuntimeException(errorMessage);
        }
    }
}

