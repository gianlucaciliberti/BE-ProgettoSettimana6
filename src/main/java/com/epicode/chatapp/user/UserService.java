package com.epicode.chatapp.user;

import com.epicode.chatapp.exception.ConflictException;
import com.epicode.chatapp.exception.NotFoundException;
import com.epicode.chatapp.user.dto.RegisterRequest;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Esiste gia' un utente con questa email");
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente non trovato: " + email));
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
