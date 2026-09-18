package com.epicode.chatapp.dto;

import java.time.LocalDateTime;

public record ChatDto(
        Long id,
        UserDto otherUser,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {
}
