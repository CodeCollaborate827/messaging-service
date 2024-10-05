package com.chat.messaging_service.config;

import static com.chat.messaging_service.exception.ErrorCode.KAFKA_MESSAGE_PARSING;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.event.Event;
import com.chat.messaging_service.event.UserRegistrationEvent;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.service.ChatUserService;
import com.chat.messaging_service.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ConsumerBindingConfig {
  private final ObjectMapper objectMapper;
  private final ChatUserService chatUserService;

  @Bean
  public Consumer<Flux<Message<Event>>> userRegistrationDownstreamConsumer() {
    return flux ->
        flux.flatMap(event -> processMessage(event.getPayload()))
            .onErrorContinue((e, object) -> log.error(e.getMessage())) // TODO: log the error
            //                .onErrorResume(this::handleError)
            .subscribe();
  }

  private Mono<ChatUser> processMessage(Event event) {
    log.info("Processing the message: {}", event);

    // increment 1.... kakfa messg counter
    String payloadBase64 = event.getPayloadBase64();
    log.info("Payload: {}", payloadBase64);
    try {
      UserRegistrationEvent userRegistrationEvent = decodeUserRegistrationEvent(payloadBase64);
      ChatUser chatUser = Utils.convertToChatUser(userRegistrationEvent);
      return chatUserService.saveNewChatUser(chatUser);
    } catch (IOException e) {
      return Mono.error(new ApplicationException(KAFKA_MESSAGE_PARSING));
    }
  }

  private Mono<ChatUser> handleError(Throwable e) {
    log.error("Error when consuming userRegistrationEvent: {}", e.getMessage());
    return Mono.just(
        new ChatUser()); // TODO: this is for handle the error, but this approach must be changed
    // later
  }

  private UserRegistrationEvent decodeUserRegistrationEvent(String base64String)
      throws IOException {
    byte[] decodedBytes = Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8));
    return objectMapper.readValue(decodedBytes, UserRegistrationEvent.class);
  }
}
