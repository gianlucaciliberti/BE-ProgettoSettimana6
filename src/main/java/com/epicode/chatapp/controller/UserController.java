package com.epicode.chatapp.controller;

import com.epicode.chatapp.dto.RegisterRequest;
import com.epicode.chatapp.dto.UserDto;
import com.epicode.chatapp.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return UserDto.from(userService.register(request));
    }

    @GetMapping
    public List<UserDto> listOtherUsers(Principal principal) {
        Long currentUserId = userService.getByUsername(principal.getName()).getId();
        return userService.getAllExcept(currentUserId).stream()
                .map(UserDto::from)
                .toList();
    }

    @GetMapping("/me")
    public UserDto me(Principal principal) {
        return UserDto.from(userService.getByUsername(principal.getName()));
    }
}
