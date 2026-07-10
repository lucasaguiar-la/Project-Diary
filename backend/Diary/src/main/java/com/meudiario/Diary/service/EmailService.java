package com.meudiario.Diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/screen/reset-password.html?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Redefinição de senha - Meu Diário");
        message.setText(
                "Você solicitou a redefinição da sua senha.\n\n" +
                "Clique no link abaixo para definir uma nova senha (válido por 30 minutos):\n" +
                resetLink + "\n\n" +
                "Se você não solicitou isso, ignore este e-mail."
        );
        mailSender.send(message);
    }
}
