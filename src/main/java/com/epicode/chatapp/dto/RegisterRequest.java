package com.epicode.chatapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Il nome utente e' obbligatorio")
        String username,

        @NotBlank(message = "L'email e' obbligatoria")
        @Email(message = "Email non valida")
        String email,

        @NotBlank(message = "La password e' obbligatoria")
        @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
        String password
) {
}
