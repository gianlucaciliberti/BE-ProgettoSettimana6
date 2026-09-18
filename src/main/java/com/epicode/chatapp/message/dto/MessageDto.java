package com.epicode.chatapp.message.dto;

import com.epicode.chatapp.message.Message;
import java.time.LocalDateTime;

public record MessageDto(
        Long id,
        Long chatId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt
) {

    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getChat().getId(),
                message.getSender().getId(),
                message.getSender().getFullName(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
