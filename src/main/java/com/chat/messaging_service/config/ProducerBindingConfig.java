package com.chat.messaging_service.config;

import com.chat.messaging_service.event.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class ProducerBindingConfig {
  public static final Sinks.Many<Message<Event>> messageEventDownstreamSink =
      Sinks.many().unicast().onBackpressureBuffer();

  public static final Sinks.Many<Message<Event>> messageMentionedNotificationDownstreamSink =
      Sinks.many().unicast().onBackpressureBuffer();

  public static final Sinks.Many<Message<Event>> messageReactedNotificationDownstreamSink =
      Sinks.many().unicast().onBackpressureBuffer();

  public static final Sinks.Many<Message<Event>> conversationEventDownstreamSink =
          Sinks.many().unicast().onBackpressureBuffer();

  @Bean("messageEventDownstream")
  public Supplier<Flux<Message<Event>>> messageEventDownstream() {
    return messageEventDownstreamSink::asFlux;
  }

  @Bean("messageMentionedNotificationDownstream")
  public Supplier<Flux<Message<Event>>> messageMentionedNotificationDownstream() {
    return messageMentionedNotificationDownstreamSink::asFlux;
  }

  @Bean("messageReactedNotificationDownstream")
  public Supplier<Flux<Message<Event>>> messageReactedNotificationDownstream() {
    return messageReactedNotificationDownstreamSink::asFlux;
  }


  @Bean("conversationEventDownstream")
  public Supplier<Flux<Message<Event>>> conversationEventDownstream() {
    return conversationEventDownstreamSink::asFlux;
  }
}
