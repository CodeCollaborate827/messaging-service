package com.chat.messaging_service.service;

import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.enums.MessageReaction;
import com.chat.messaging_service.event.downstream.MessageEvent;

public interface KafkaProducerService {
  // for sending to notification manager for creating notification
  void sendMessageMentionedNotificationEventToKafka(
      ConversationMessage message, String mentionedMemberId);

  // for sending to notification manager for creating notification
  void sendMessageReactedNotificationEventToKafka(
      String reactionSenderId, ConversationMessage message, MessageReaction reaction);

  // for sending to realtime service for realtime update
  void sendMessageEvent(MessageEvent messageEvent);
}
