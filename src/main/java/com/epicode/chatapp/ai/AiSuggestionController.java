package com.epicode.chatapp.ai;

import com.epicode.chatapp.ai.dto.AiSuggestionResponse;
import com.epicode.chatapp.user.User;
import com.epicode.chatapp.user.UserService;
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
        User current = userService.getByEmail(principal.getName());
        String suggestion = aiSuggestionService.suggestNextMessage(current, chatId);
        return new AiSuggestionResponse(suggestion);
    }
}
