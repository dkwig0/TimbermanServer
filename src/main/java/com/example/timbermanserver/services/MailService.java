package com.example.timbermanserver.services;

import com.example.timbermanserver.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private MailSender mailSender;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.activationUrl}")
    private String activationUrl;

    public void sendActivationCodeToUser(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Timberman account activation");
        message.setText(String.format(
                "Hello, %s! To activate your account, please, visit the link: " +
                        "%s%s?c=%s&u=%s",
                user.getUsername(),
                domain,
                activationUrl,
                user.getActivationCode(),
                user.getUsername()
                ));
        mailSender.send(message);
    }

}
