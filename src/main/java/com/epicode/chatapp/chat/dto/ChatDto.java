package com.epicode.chatapp.chat.dto;

import com.epicode.chatapp.user.dto.UserDto;
import java.time.LocalDateTime;

public record ChatDto(
        Long id,
        UserDto otherUser,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {
}
