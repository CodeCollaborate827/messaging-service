package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.ConversationMessage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<ConversationMessage, String> {}
