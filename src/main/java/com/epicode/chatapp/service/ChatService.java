package com.epicode.chatapp.service;

import com.epicode.chatapp.exception.ForbiddenException;
import com.epicode.chatapp.exception.NotFoundException;
import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.entities.Message;
import com.epicode.chatapp.repository.ChatRepository;
import com.epicode.chatapp.repository.MessageRepository;
import com.epicode.chatapp.dto.ChatDto;
import com.epicode.chatapp.dto.UserDto;
import com.epicode.chatapp.entities.User;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public Chat getOrCreate(User current, User other) {
        if (current.getId().equals(other.getId())) {
            throw new ForbiddenException("Non puoi aprire una chat con te stesso");
        }
        User userA = current.getId() < other.getId() ? current : other;
        User userB = current.getId() < other.getId() ? other : current;

        return chatRepository.findByUserAAndUserB(userA, userB)
                .orElseGet(() -> chatRepository.save(
                        Chat.builder().userA(userA).userB(userB).build()
                ));
    }

    public Chat getByIdForParticipant(Long chatId, User participant) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat non trovata: " + chatId));
        if (!chat.hasParticipant(participant)) {
            throw new ForbiddenException("Non fai parte di questa chat");
        }
        return chat;
    }

    public List<ChatDto> getChatsForUser(User user) {
        return chatRepository.findByUserAOrUserB(user, user).stream()
                .map(chat -> toDto(chat, user))
                .sorted(Comparator.comparing(ChatDto::lastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private ChatDto toDto(Chat chat, User user) {
        Message lastMessage = messageRepository.findTopByChatOrderBySentAtDesc(chat).orElse(null);
        return new ChatDto(
                chat.getId(),
                UserDto.from(chat.theOtherUser(user)),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getSentAt() : chat.getCreatedAt()
        );
    }
}
