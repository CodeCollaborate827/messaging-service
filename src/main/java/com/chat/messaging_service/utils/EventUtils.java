package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.enums.MessageReaction;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.downstream.ConversationEvent;
import com.chat.messaging_service.event.downstream.MessageEvent;
import com.chat.messaging_service.event.downstream.conversation.NewConversationEventData;
import com.chat.messaging_service.event.downstream.message.MessageMentionedNotificationTriggerEvent;
import com.chat.messaging_service.event.downstream.message.MessageReactedNotificationTriggerEvent;
import com.chat.messaging_service.event.downstream.message.MessageReactionEventData;
import com.chat.messaging_service.event.downstream.message.NewMessageEventData;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Instant;
import java.util.List;

public class EventUtils {
  public static MessageEvent buildMessageEventNewMessage(
      Conversation conversation, ConversationMessage message) {
    // prepare the event data
    List<String> list = conversation.getMemberIds();
    NewMessageEventData newMessageEventData =
        NewMessageEventData.builder()
            .senderId(message.getSenderId())
            .messageContent(message.getContent())
            .messageId(message.getId())
            .messageCreatedAt(message.getCreatedAt())
            .repliedMessageId(message.getRepliedMessageId())
            .mentionedMemberIds(message.getMentionedMemberIds())
            .build();

    // create message event
    MessageEvent messageEvent =
        MessageEvent.builder()
            .messageType(MessageEvent.MessageEventType.MESSAGE_NEW)
            .messageId(message.getId())
            .conversationId(conversation.getId())
            .conversationMemberIds(conversation.getMemberIds())
            .data(newMessageEventData)
            .build();

    return messageEvent;
  }

  public static MessageEvent buildMessageEventMessageReaction(
      Conversation conversation,
      ConversationMessage message,
      String reactionSenderId,
      MessageReaction reaction,
      boolean messageReacted) {
    // create event data for message reaction
    MessageReactionEventData eventData =
        MessageReactionEventData.builder()
            .reactionSenderId(reactionSenderId)
            .reaction(reaction)
            .unReacted(!messageReacted)
            .timestamp(Instant.now().getEpochSecond())
            .build();

    return MessageEvent.builder()
        .messageType(MessageEvent.MessageEventType.MESSAGE_REACTED)
        .messageId(message.getId())
        .conversationId(conversation.getId())
        .conversationMemberIds(conversation.getMemberIds())
        .data(eventData)
        .build();
  }

  public static Event buildNotificationEventFoMessageMention(
      ConversationMessage message, String mentionedMemberId) throws JsonProcessingException {
    // create MessageMentionedNotificationTriggerEvent
    MessageMentionedNotificationTriggerEvent messageMentionedEventData =
        MessageMentionedNotificationTriggerEvent.builder()
            .messageId(message.getId())
            .conversationId(message.getConversationId())
            .messageSenderId(message.getSenderId())
            .mentionedMemberId(mentionedMemberId)
            .messageContent(message.getContent())
            .createdAt(message.getCreatedAt())
            .build();

    // create kafka message
    return constructEvent(messageMentionedEventData);
  }

  public static Event buildNotificationEventFoMessageReacted(
      String reactionSenderId, ConversationMessage message, MessageReaction reaction)
      throws JsonProcessingException {
    // create MessageMentionedNotificationTriggerEvent
    MessageReactedNotificationTriggerEvent messageReactedNotificationTriggerEvent =
        MessageReactedNotificationTriggerEvent.builder()
            .messageId(message.getId())
            .conversationId(message.getConversationId())
            .reactionSenderId(reactionSenderId)
            .messageSenderId(message.getSenderId()) // who will get the notification
            .messageContent(message.getContent())
            .reaction(reaction)
            .createdAt(Instant.now().getEpochSecond())
            .build();

    // create kafka message
    return constructEvent(messageReactedNotificationTriggerEvent);
  }

  public static Event buildNewConversationEvent(
      ConversationEvent.ConversationEventType eventType, Conversation conversation)
      throws JsonProcessingException {
    NewConversationEventData newConversationEventData =
        NewConversationEventData.builder()
            .conversationId(conversation.getId())
            .memberDetails(conversation.getMemberDetails())
            .conversationType(conversation.getConversationType())
            .groupConversationName(conversation.getGroupConversationName())
            .groupConversationAvatar(conversation.getGroupConversationAvatar())
            .createdAt(conversation.getCreatedAt())
            .build();

    ConversationEvent event =
        ConversationEvent.builder()
            .conversationEventType(eventType)
            .conversationId(conversation.getId())
            .conversationMemberIds(conversation.getMemberIds())
            .timestamp(System.currentTimeMillis())
            // TODO: remove and add the necessary data to the event
            .data(newConversationEventData)
            .build();

    return constructEvent(event);
  }

  private static Event constructEvent(Object event) throws JsonProcessingException {
    String payloadBase64 = Utils.encodeBase64(event);
    return Event.builder().type(event.getClass().toString()).payloadBase64(payloadBase64).build();
  }
}
