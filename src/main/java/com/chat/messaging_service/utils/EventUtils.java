package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.downstream.conversation.ConversationEvent;
import com.chat.messaging_service.event.downstream.message.NewMessageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

public class EventUtils {
  public static Event buildNewMessageEvent(Conversation conversation, ConversationMessage message)
      throws JsonProcessingException {
    List<String> list = conversation.getMemberIds();
    NewMessageEvent newMessageEvent =
        NewMessageEvent.builder()
            .senderId(message.getSenderId())
            .messageContent(message.getContent())
            .messageId(message.getId())
            .messageCreatedAt(message.getCreatedAt().toEpochSecond())
            .repliedMessageId(message.getRepliedMessageId())
            .conversationId(conversation.getId())
            .conversationMemberIds(list)
            .build();

    return constructEvent(newMessageEvent);
  }

  public static Event buildNewConversationEvent(
      ConversationEvent.ConversationEventType eventType, Conversation savedConversation)
      throws JsonProcessingException {
    ConversationEvent event =
        ConversationEvent.builder()
            .conversationEventType(eventType)
            .conversationId(savedConversation.getId())
            .conversationMemberIds(savedConversation.getMemberIds())
            .timestamp(System.currentTimeMillis())
            // TODO: remove and add the necessary data to the event
            .data(savedConversation)
            .build();

    return constructEvent(event);
  }

  private static Event constructEvent(Object event) throws JsonProcessingException {
    String payloadBase64 = Utils.encodeBase64(event);
    return Event.builder().type(event.getClass().toString()).payloadBase64(payloadBase64).build();
  }
}
