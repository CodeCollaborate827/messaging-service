package com.chat.messaging_service.service;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;

public interface KafkaProducerService {
  void sendNewMessageEventToKafka(Conversation savedConversation, ConversationMessage message);
}
