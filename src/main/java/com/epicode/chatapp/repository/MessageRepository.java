package com.epicode.chatapp.repository;

import com.epicode.chatapp.entities.Chat;
import com.epicode.chatapp.entities.Message;
import com.epicode.chatapp.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatOrderBySentAtAsc(Chat chat);

    List<Message> findTop10ByChatOrderBySentAtDesc(Chat chat);

    Optional<Message> findTopByChatOrderBySentAtDesc(Chat chat);

    long countBySender(User sender);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE (m.chat.userA = :user OR m.chat.userB = :user) AND m.sender <> :user")
    long countReceivedByUser(@Param("user") User user);
}
