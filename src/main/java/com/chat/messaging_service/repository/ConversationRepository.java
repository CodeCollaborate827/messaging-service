package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.Conversation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, String> {

  // Method to retrieve all conversations sorted by updatedAt descending
  Flux<Conversation> findAllIdByOrderByUpdatedAtDesc(List<String> conversationIds);

  // Find direct conversations between two users based on their IDs
  @Query(
          "{ 'conversation_type' : 'DIRECT', 'member_ids' : { $all: [ ?0, ?1 ] } }")
  Mono<Conversation> findDirectConversationBetweenUsers(String userId1, String userId2);

  @Query(
          "{ 'conversation_type' : 'SELF', 'member_ids' : { $all: [ ?0 ] } }")
  Mono<Conversation> findSelfConversation(String userId1);

}
