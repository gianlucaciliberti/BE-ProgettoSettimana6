package com.epicode.chatapp.service;

import com.epicode.chatapp.repository.ChatRepository;
import com.epicode.chatapp.repository.MessageRepository;
import com.epicode.chatapp.dto.UserStatsDto;
import com.epicode.chatapp.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public UserStatsDto getStats(User user) {
        long sent = messageRepository.countBySender(user);
        long received = messageRepository.countReceivedByUser(user);
        long openChats = chatRepository.countByUserAOrUserB(user, user);
        return new UserStatsDto(sent, received, openChats);
    }
}
