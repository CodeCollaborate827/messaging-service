package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.config.ProducerBindingConfig;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.enums.MessageReaction;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.downstream.MessageEvent;
import com.chat.messaging_service.event.downstream.ConversationEvent;
import com.chat.messaging_service.service.KafkaProducerService;
import com.chat.messaging_service.utils.EventUtils;
import com.chat.messaging_service.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

  @Override
  public void sendNotificationTriggerEventForMessageMentioned(
      ConversationMessage message, String mentionedMemberId) {
    try {
      Event messageMentionedEvent =
          EventUtils.buildNotificationEventFoMessageMention(message, mentionedMemberId);
      tryEmitEvent(
          messageMentionedEvent, ProducerBindingConfig.messageMentionedNotificationDownstreamSink);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public void sendNotificationTriggerEventForMessageReaction(
      String reactionSenderId, ConversationMessage message, MessageReaction reaction) {
    try {
      Event messageReactedNotificationEvent =
          EventUtils.buildNotificationEventFoMessageReacted(reactionSenderId, message, reaction);
      tryEmitEvent(
          messageReactedNotificationEvent,
          ProducerBindingConfig.messageReactedNotificationDownstreamSink);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public void sendMessageEvent(MessageEvent messageEvent) {
    try {
      String payload64 = Utils.encodeBase64(messageEvent);
      Event event =
          Event.builder().type(messageEvent.getClass().toString()).payloadBase64(payload64).build();
      tryEmitEvent(event, ProducerBindingConfig.messageEventDownstreamSink);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
  }

  private void tryEmitEvent(Event event, Sinks.Many<Message<Event>> sink) {
    Message<Event> kafkaMessage = MessageBuilder.withPayload(event).build();

    Sinks.EmitResult emitResult = sink.tryEmitNext(kafkaMessage);

    if (emitResult.isFailure()) {
      // TODO: add more detail log message here
      log.error("Failed to emit new registry event: {}", emitResult);
    } else {
      log.info("Event emitted successfully");
    }
  }

  @Override
  public void sendNewConversationEventToKafka(
          ConversationEvent.ConversationEventType eventType, Conversation conversation) {
    try {

      Event newConversationEvent =
          EventUtils.buildNewConversationEvent(eventType, conversation);
      Message<Event> eventMessage = MessageBuilder.withPayload(newConversationEvent).build();
      emitEvent(ProducerBindingConfig.conversationEventDownstreamSink, eventMessage);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void emitEvent(Sinks.Many<Message<Event>> sink, Message<Event> message) {
    Sinks.EmitResult emitResult = sink.tryEmitNext(message);
    if (emitResult.isFailure()) {
      log.error("Failed to emit new registry event: {}", emitResult);
    } else {
      log.info("Event emitted successfully");
    }
  }
}
