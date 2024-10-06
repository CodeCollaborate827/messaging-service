package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.Conversation;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConversationRepository extends ReactiveCrudRepository<Conversation, String> {

  // Method to retrieve all conversations sorted by updatedAt descending
  Flux<Conversation> findAllIdByOrderByUpdatedAtDesc(List<String> conversationIds);

  // Find direct conversations between two users based on their IDs
  @Query(
      "{ 'isGroupConversation' : false, 'members' : { $size: 2, $all: [ { $elemMatch: { 'id': ?0 } }, { $elemMatch: { 'id': ?1 } } ] } }")
  Mono<Conversation> findDirectConversationBetweenUsers(String userId1, String userId2);
}
