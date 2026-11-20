package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.dto.ContactRequest;
import com.thomas.rgsga.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${application.contact.recipient-email:tk5808676@gmail.com}")
    private String recipientEmail;

    @Value("${spring.mail.username:tk5808676@gmail.com}")
    private String senderEmail;

    public EmailServiceImpl(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendContactQuery(ContactRequest request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String senderName = request.getName() != null ? request.getName().trim() : "Anonymous User";
        String senderAddress = request.getEmail() != null ? request.getEmail().trim() : "no-reply@example.com";
        String inquiryMessage = request.getMessage() != null ? request.getMessage().trim() : "";

        String subject = (request.getSubject() != null && !request.getSubject().isBlank())
                ? "[CareerCraft AI Inquiry] " + request.getSubject().trim()
                : "[CareerCraft AI Inquiry] New Message from " + senderName;

        String body = String.format(
                "You have received a new inquiry from the CareerCraft AI portal:\n\n" +
                "==================================================\n" +
                "Sender Name: %s\n" +
                "Sender Email: %s\n" +
                "Timestamp:   %s\n" +
                "==================================================\n\n" +
                "Message Content:\n" +
                "%s\n\n" +
                "==================================================\n" +
                "Direct Reply: You can reply directly to this email to contact the user at %s.",
                senderName,
                senderAddress,
                timestamp,
                inquiryMessage,
                senderAddress
        );

        log.info("Processing contact query from [{}] <{}> dispatched to administrator: [{}]",
                senderName, senderAddress, recipientEmail);

        if (mailSender != null) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(senderEmail);
                mailMessage.setTo(recipientEmail);
                mailMessage.setReplyTo(senderAddress);
                mailMessage.setSubject(subject);
                mailMessage.setText(body);

                mailSender.send(mailMessage);
                log.info("Contact email successfully sent via SMTP to {}", recipientEmail);
            } catch (Exception ex) {
                // If SMTP is currently not configured or network restricts SMTP port, log query details safely
                log.warn("SMTP send failed ({}). Contact query logged successfully for {}: {}",
                        ex.getMessage(), recipientEmail, inquiryMessage);
            }
        } else {
            log.info("JavaMailSender bean not configured. Query logged safely for administrator {}: {}",
                    recipientEmail, body);
        }
    }
}
