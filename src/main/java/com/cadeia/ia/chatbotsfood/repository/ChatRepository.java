package com.cadeia.ia.chatbotsfood.repository;

import com.cadeia.ia.chatbotsfood.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatIdOrderByTimestampAsc(String chatId);
}
