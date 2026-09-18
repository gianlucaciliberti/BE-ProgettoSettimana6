package com.epicode.chatapp.dto;

import jakarta.validation.constraints.NotNull;

public record NewChatRequest(

        @NotNull(message = "otherUserId e' obbligatorio")
        Long otherUserId
) {
}
