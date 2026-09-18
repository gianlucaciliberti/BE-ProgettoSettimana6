package com.epicode.chatapp.chat;

import com.epicode.chatapp.message.MessageService;
import com.epicode.chatapp.service.ChatDto;
import com.epicode.chatapp.service.MessageDto;
import com.epicode.chatapp.service.NewChatRequest;
import com.epicode.chatapp.service.UserDto;
import com.epicode.chatapp.user.User;
import com.epicode.chatapp.user.UserService;
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
