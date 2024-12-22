package com.chat.messaging_service.config;

import com.chat.messaging_service.event.Event;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class ProducerBindingConfig {
  public static final Sinks.Many<Message<Event>> newMessageDownstreamSink =
      Sinks.many().unicast().onBackpressureBuffer();

  @Bean("newMessageDownstream")
  public Supplier<Flux<Message<Event>>> newMessageDownstream() {
    return newMessageDownstreamSink::asFlux;
  }
}
