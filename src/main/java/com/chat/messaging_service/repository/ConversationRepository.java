package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.Conversation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, String> {

    // Method to retrieve all conversations sorted by updatedAt descending
    Flux<Conversation> findAllIdByOrderByUpdatedAtDesc(List<String> conversationIds);

}
