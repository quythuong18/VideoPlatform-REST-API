package com.qt.VideoPlatformAPI.Verification;


import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private JavaMailSender emailSender;

    public void send(String to, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@videoplatform.com");
        message.setTo(to);
        message.setSubject(subject);

        emailSender.send(message);
    }
}
