package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.downstream.NewMessageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

public class EventUtils {
  public static Event buildNewMessageEvent(Conversation conversation, ConversationMessage message)
      throws JsonProcessingException {
    List<String> list = conversation.getMembers().stream().map(u -> u.getId()).toList();
    NewMessageEvent newMessageEvent =
        NewMessageEvent.builder()
            .messageContent(message.getContent())
            .messageId(message.getId())
            .messageCreatedAt(message.getCreatedAt())
            .repliedMessageId(message.getRepliedMessageId())
            .conversationId(conversation.getId())
            .conversationMemberIds(list)
            .build();

    String payloadBase64 = Utils.encodeBase64(newMessageEvent);

    return Event.builder()
        .type(newMessageEvent.getClass().toString())
        .payloadBase64(payloadBase64)
        .build();
  }
}
