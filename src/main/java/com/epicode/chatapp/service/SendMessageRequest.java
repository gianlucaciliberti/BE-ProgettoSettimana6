package com.epicode.chatapp.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(

        @NotNull(message = "chatId e' obbligatorio")
        Long chatId,

        @NotBlank(message = "Il contenuto del messaggio e' obbligatorio")
        String content
) {
}
