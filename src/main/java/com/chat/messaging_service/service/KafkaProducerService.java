package com.chat.messaging_service.service;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.event.downstream.conversation.ConversationEvent;

public interface KafkaProducerService {
  void sendNewMessageEventToKafka(Conversation savedConversation, ConversationMessage message);

  void sendNewConversationEventToKafka(
      ConversationEvent.ConversationEventType eventType, Conversation savedConversation);
}
