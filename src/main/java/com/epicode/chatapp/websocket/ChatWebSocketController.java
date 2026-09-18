package com.epicode.chatapp.websocket;

import com.epicode.chatapp.chat.Chat;
import com.epicode.chatapp.chat.ChatService;
import com.epicode.chatapp.message.MessageService;
import com.epicode.chatapp.message.dto.MessageDto;
import com.epicode.chatapp.message.dto.SendMessageRequest;
import com.epicode.chatapp.user.User;
import com.epicode.chatapp.user.UserService;
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
        User sender = userService.getByEmail(principal.getName());
        MessageDto saved = messageService.sendMessage(sender, request.chatId(), request.content());

        Chat chat = chatService.getByIdForParticipant(request.chatId(), sender);
        User recipient = chat.theOtherUser(sender);

        messagingTemplate.convertAndSendToUser(recipient.getEmail(), USER_QUEUE, saved);
        messagingTemplate.convertAndSendToUser(sender.getEmail(), USER_QUEUE, saved);
    }
}
