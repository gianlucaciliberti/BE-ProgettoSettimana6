package com.epicode.chatapp.stats.dto;

public record UserStatsDto(long messagesSent, long messagesReceived, long openChats) {
}
