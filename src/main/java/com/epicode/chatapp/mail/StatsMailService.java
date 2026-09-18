package com.epicode.chatapp.mail;

import com.epicode.chatapp.stats.StatsService;
import com.epicode.chatapp.stats.dto.UserStatsDto;
import com.epicode.chatapp.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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
        context.setVariable("fullName", user.getFullName());
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
        } catch (MessagingException e) {
            throw new IllegalStateException("Invio email delle statistiche fallito", e);
        }
    }
}
