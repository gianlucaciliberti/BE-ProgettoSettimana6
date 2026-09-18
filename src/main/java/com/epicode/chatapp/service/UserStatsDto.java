package com.epicode.chatapp.service;

public record UserStatsDto(long messagesSent, long messagesReceived, long openChats) {
}
