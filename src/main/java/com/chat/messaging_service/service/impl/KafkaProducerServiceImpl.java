package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.config.ProducerBindingConfig;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.event.Event;
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

      Sinks.EmitResult emitResult =
          ProducerBindingConfig.newMessageDownstreamSink.tryEmitNext(kafkaMessage);

      if (emitResult.isFailure()) {
        log.error("Failed to emit new registry event: {}", emitResult);
      } else {
        log.info("Event emitted successfully");
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
