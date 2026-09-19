package com.epicode.chatapp.controller;

import com.epicode.chatapp.service.StatsMailService;
import com.epicode.chatapp.service.StatsService;
import com.epicode.chatapp.dto.UserStatsDto;
import com.epicode.chatapp.entities.User;
import com.epicode.chatapp.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final StatsMailService statsMailService;
    private final UserService userService;

    @GetMapping("/me")
    public UserStatsDto myStats(Principal principal) {
        return statsService.getStats(userService.getByUsername(principal.getName()));
    }

    @PostMapping("/me/email")
    public ResponseEntity<Void> emailMyStats(Principal principal) {
        User user = userService.getByUsername(principal.getName());
        statsMailService.sendStatsEmail(user);
        return ResponseEntity.accepted().build();
    }
}
