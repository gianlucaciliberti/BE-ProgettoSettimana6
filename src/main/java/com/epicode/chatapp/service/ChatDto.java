package com.epicode.chatapp.service;

import java.time.LocalDateTime;

public record ChatDto(
        Long id,
        UserDto otherUser,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {
}
