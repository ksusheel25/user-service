package com.user_service.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static Map<String ,String> map = new HashMap<>();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;\">" +
                    "<h2>Password Reset Request</h2>" +
                    "<p>Hi there,</p>" +
                    "<p>We received a request to reset your password. Click the button below to reset it:</p>" +
                    "<a href=\"" + resetLink + "\" style=\"display: inline-block; padding: 10px 20px; margin-top: 20px; color: #fff; text-decoration: none; background-color: #007BFF; border-radius: 4px;\">Reset Your Password</a>" +
                    "<p>If you didn't request a password reset, you can ignore this email. Your password will remain unchanged.</p>" +
                    "<p>Thank you, <br/>The Team</p>" +
                    "</div>";

            helper.setText(htmlContent, true); // Set 'true' to enable HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle the exception appropriately (log it, rethrow it, etc.)
        }
    }

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(message.getFrom());
        message.setSubject("Email Verification");
        message.setText("Please verify your email by clicking the following link: " + verificationLink);
        mailSender.send(message);
    }
}
