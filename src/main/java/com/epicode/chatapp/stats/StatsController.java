package com.epicode.chatapp.stats;

import com.epicode.chatapp.mail.StatsMailService;
import com.epicode.chatapp.service.UserStatsDto;
import com.epicode.chatapp.user.User;
import com.epicode.chatapp.user.UserService;
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
        return statsService.getStats(userService.getByEmail(principal.getName()));
    }

    @PostMapping("/me/email")
    public ResponseEntity<Void> emailMyStats(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        statsMailService.sendStatsEmail(user);
        return ResponseEntity.accepted().build();
    }
}
