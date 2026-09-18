package com.epicode.chatapp.service;

import com.epicode.chatapp.exception.EmailDeliveryException;
import com.epicode.chatapp.dto.UserStatsDto;
import com.epicode.chatapp.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class StatsMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final StatsService statsService;

    public void sendStatsEmail(User user) {
        UserStatsDto stats = statsService.getStats(user);

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("messagesSent", stats.messagesSent());
        context.setVariable("messagesReceived", stats.messagesReceived());
        context.setVariable("openChats", stats.openChats());
        String html = templateEngine.process("email/stats-email", context);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Le tue statistiche chat");
            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException(
                    "Invio email fallito: controlla le credenziali SMTP (MAIL_USERNAME/MAIL_APP_PASSWORD) in application-local.properties",
                    e
            );
        }
    }
}
