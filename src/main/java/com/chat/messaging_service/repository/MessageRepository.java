package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.ChatUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {
}
