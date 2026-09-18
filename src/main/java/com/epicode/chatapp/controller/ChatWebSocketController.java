package com.epicode.chatapp.controller;

import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.service.ChatService;
import com.epicode.chatapp.service.MessageService;
import com.epicode.chatapp.dto.MessageDto;
import com.epicode.chatapp.dto.SendMessageRequest;
import com.epicode.chatapp.entities.User;
import com.epicode.chatapp.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private static final String USER_QUEUE = "/queue/messages";

    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void send(SendMessageRequest request, Principal principal) {
        User sender = userService.getByUsername(principal.getName());
        MessageDto saved = messageService.sendMessage(sender, request.chatId(), request.content());

        Chat chat = chatService.getByIdForParticipant(request.chatId(), sender);
        User recipient = chat.theOtherUser(sender);

        messagingTemplate.convertAndSendToUser(recipient.getUsername(), USER_QUEUE, saved);
        messagingTemplate.convertAndSendToUser(sender.getUsername(), USER_QUEUE, saved);
    }
}
