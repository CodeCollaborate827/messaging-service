package com.chat.messaging_service.config;

import com.chat.messaging_service.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ConsumerBindingConfig {

    @Bean
    public Consumer<Flux<Message<Event>>> userRegistrationDownstreamConsumer() {
        return flux -> flux.flatMap(event -> processMessage(event.getPayload()))
                .onErrorResume(this::handleError)
                .subscribe();
    }

    private Mono<String> processMessage(Event event) {
        log.info("Processing the message");

        // increment 1.... kakfa messg counter
        log.info("Payload: {}", event.getPayloadBase64());
        return Mono.empty();
    }


    private Mono<String> handleError(Throwable e) {
        log.error("Error when consuming userRegistrationEvent: {}", e.getMessage());
        return Mono.just("Error");
    }

}
