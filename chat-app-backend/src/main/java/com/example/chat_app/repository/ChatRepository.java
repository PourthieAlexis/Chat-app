package com.example.chat_app.repository;

import com.example.chat_app.model.Chat;
import com.example.chat_app.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @EntityGraph(attributePaths = {"messageList"})
    Chat findWithMessagesById(Long id);
    
    @Query("SELECT c FROM Chat c WHERE (c.firstUser = :user1 AND c.secondUser = :user2) OR (c.firstUser = :user2 AND c.secondUser = :user1)")
    Optional<Chat> findChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
}
