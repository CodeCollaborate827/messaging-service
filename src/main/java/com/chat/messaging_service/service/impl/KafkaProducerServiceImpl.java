package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.config.ProducerBindingConfig;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.downstream.conversation.ConversationEvent;
import com.chat.messaging_service.service.KafkaProducerService;
import com.chat.messaging_service.utils.EventUtils;
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
  public void sendNewMessageEventToKafka(Conversation conversation, ConversationMessage message) {
    try {
      Event newMessageEvent = EventUtils.buildNewMessageEvent(conversation, message);
      Message<Event> kafkaMessage = MessageBuilder.withPayload(newMessageEvent).build();
      emitEvent(ProducerBindingConfig.newMessageDownstreamSink, kafkaMessage);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendNewConversationEventToKafka(
      ConversationEvent.ConversationEventType eventType, Conversation savedConversation) {
    try {
      Event newConversationEvent =
          EventUtils.buildNewConversationEvent(eventType, savedConversation);
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
