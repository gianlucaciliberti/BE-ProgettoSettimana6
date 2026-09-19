package com.epicode.chatapp.controller;

import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.service.ChatService;
import com.epicode.chatapp.service.MessageService;
import com.epicode.chatapp.dto.ChatDto;
import com.epicode.chatapp.dto.MessageDto;
import com.epicode.chatapp.dto.NewChatRequest;
import com.epicode.chatapp.dto.UserDto;
import com.epicode.chatapp.entities.User;
import com.epicode.chatapp.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping
    public List<ChatDto> listChats(Principal principal) {
        User current = userService.getByUsername(principal.getName());
        return chatService.getChatsForUser(current);
    }

    @PostMapping
    public ChatDto openOrCreateChat(@Valid @RequestBody NewChatRequest request, Principal principal) {
        User current = userService.getByUsername(principal.getName());
        User other = userService.getById(request.otherUserId());
        Chat chat = chatService.getOrCreate(current, other);
        return new ChatDto(chat.getId(), UserDto.from(other), null, chat.getCreatedAt());
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(@PathVariable Long chatId, Principal principal) {
        User current = userService.getByUsername(principal.getName());
        return messageService.getHistory(current, chatId);
    }
}
