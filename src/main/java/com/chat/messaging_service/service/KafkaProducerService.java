package com.chat.messaging_service.service;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.enums.MessageReaction;
import com.chat.messaging_service.event.downstream.MessageEvent;
import com.chat.messaging_service.event.downstream.ConversationEvent;

public interface KafkaProducerService {
  // for sending to notification manager for creating notification
  void sendNotificationTriggerEventForMessageMentioned(
      ConversationMessage message, String mentionedMemberId);

  // for sending to notification manager for creating notification
  void sendNotificationTriggerEventForMessageReaction(
      String reactionSenderId, ConversationMessage message, MessageReaction reaction);

  // for sending to realtime service for realtime update
  void sendMessageEvent(MessageEvent messageEvent);


  void sendNewConversationEventToKafka(
          ConversationEvent.ConversationEventType eventType, Conversation savedConversation);
}
