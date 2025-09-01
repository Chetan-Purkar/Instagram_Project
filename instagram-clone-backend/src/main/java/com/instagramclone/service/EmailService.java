package com.instagramclone.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class EmailService {
	
    private final JavaMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.mail.from}")
    private String fromEmail; 

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp, Duration validity) {
        String subject = "Your OTP to login";
        String body = buildHtmlOtpBody(otp, validity);

        sendHtmlEmailWithRetry(to, subject, body, 3);
    }

    private String buildHtmlOtpBody(String otp, Duration validity) {
        return """
        <html>
          <body>
            <p>Hi,</p>
            <p>Your OTP for login is: <strong>%s</strong></p>
            <p>This OTP is valid for %d minutes.</p>
            <p>If you did not request this, please ignore this email.</p>
          </body>
        </html>
        """.formatted(otp, validity.toMinutes());
    }

    private void sendHtmlEmailWithRetry(String to, String subject, String htmlBody, int maxAttempts) {
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                helper.setFrom(fromEmail);
                mailSender.send(message);
                return;
            } catch (MailException | MessagingException e) {
                attempt++;
                log.warn("Failed to send email to {} (attempt {}): {}", to, attempt, e.getMessage());
                try { Thread.sleep(500L * attempt); } catch (InterruptedException ignored) {}
            }
        }
        log.error("Failed to send email to {} after {} attempts", to, maxAttempts);
        throw new IllegalStateException("Unable to send email");
    }
}
