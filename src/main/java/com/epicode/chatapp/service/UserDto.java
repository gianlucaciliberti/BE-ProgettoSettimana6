package com.epicode.chatapp.service;

import com.epicode.chatapp.user.User;

public record UserDto(Long id, String fullName, String email) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail());
    }
}
