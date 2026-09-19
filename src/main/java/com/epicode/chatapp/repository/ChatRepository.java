package com.epicode.chatapp.repository;

import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByUserAAndUserB(User userA, User userB);

    List<Chat> findByUserAOrUserB(User userA, User userB);

    long countByUserAOrUserB(User userA, User userB);
}
