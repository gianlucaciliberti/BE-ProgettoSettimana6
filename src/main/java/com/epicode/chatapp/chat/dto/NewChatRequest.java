package com.epicode.chatapp.chat.dto;

import jakarta.validation.constraints.NotNull;

public record NewChatRequest(

        @NotNull(message = "otherUserId e' obbligatorio")
        Long otherUserId
) {
}
