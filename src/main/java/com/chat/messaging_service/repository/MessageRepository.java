package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.ConversationMessage;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<ConversationMessage, String> {
  Flux<ConversationMessage> findAllByConversationIdAndMessageNoGreaterThanEqual(
      String conversationId, long start);

  @Query("{ 'conversationId': ?0, 'messageNo': { $gte: ?1, $lte: ?2 } }")
  Flux<ConversationMessage> findAllByConversationIdAndMessageNoBetweenInclusive(
      String conversationId, long start, long end);
}
