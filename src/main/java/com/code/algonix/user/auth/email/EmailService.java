package com.code.algonix.user.auth.email;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.subject.reset-password:Parolni tiklash}")
    private String resetPasswordSubject;

    public void sendResetLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(resetPasswordSubject);
        message.setText("Parolni tiklash uchun quyidagi havolani bosing:\n\n" + resetLink);
        message.setFrom(fromEmail);  // Yoki mailSender.getUsername()

        mailSender.send(message);
    }
}

