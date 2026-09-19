package com.epicode.chatapp.controller;

import com.epicode.chatapp.dto.AiSuggestionResponse;
import com.epicode.chatapp.entities.User;
import com.epicode.chatapp.service.AiSuggestionService;
import com.epicode.chatapp.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats/{chatId}/ai-suggest")
@RequiredArgsConstructor
public class AiSuggestionController {

    private final AiSuggestionService aiSuggestionService;
    private final UserService userService;

    @PostMapping
    public AiSuggestionResponse suggest(@PathVariable Long chatId, Principal principal) {
        User current = userService.getByUsername(principal.getName());
        String suggestion = aiSuggestionService.suggestNextMessage(current, chatId);
        return new AiSuggestionResponse(suggestion);
    }
}
