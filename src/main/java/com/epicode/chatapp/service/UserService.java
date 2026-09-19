package com.epicode.chatapp.service;

import com.epicode.chatapp.exception.ConflictException;
import com.epicode.chatapp.exception.NotFoundException;
import com.epicode.chatapp.dto.RegisterRequest;
import com.epicode.chatapp.entities.User;
import com.epicode.chatapp.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Esiste gia' un utente con questo nome utente");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Esiste gia' un utente con questa email");
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Utente non trovato: " + username));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente non trovato: " + id));
    }

    public List<User> getAllExcept(Long currentUserId) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .toList();
    }
}
