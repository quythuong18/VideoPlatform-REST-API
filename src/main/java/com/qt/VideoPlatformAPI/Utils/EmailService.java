package com.qt.VideoPlatformAPI.Utils;


import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailService {

    private JavaMailSender emailSender;

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@videoplatform.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            emailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        }
        catch(MailException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
