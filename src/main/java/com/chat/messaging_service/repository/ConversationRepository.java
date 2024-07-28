package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.Conversation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, String> {
}
