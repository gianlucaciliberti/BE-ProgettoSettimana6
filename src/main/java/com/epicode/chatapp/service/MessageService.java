package com.epicode.chatapp.service;

import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.dto.MessageDto;
import com.epicode.chatapp.entities.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;

    public MessageDto sendMessage(User sender, Long chatId, String content) {
        Chat chat = chatService.getByIdForParticipant(chatId, sender);
        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .build();
        return MessageDto.from(messageRepository.save(message));
    }

    public List<MessageDto> getHistory(User requester, Long chatId) {
        Chat chat = chatService.getByIdForParticipant(chatId, requester);
        return messageRepository.findByChatOrderBySentAtAsc(chat).stream()
                .map(MessageDto::from)
                .toList();
    }
}
