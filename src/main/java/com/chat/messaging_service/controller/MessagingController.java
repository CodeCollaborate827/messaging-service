package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.ReactMessageRequest;
import com.chat.messaging_service.dto.request.SendMessageToConversationRequest;
import com.chat.messaging_service.dto.request.SendMessageToUserRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessagingController {
  private final MessagingService messagingService;

  // when there is no conversation between 2 users
  // , this endpoint will create a new conversation for them
  @PostMapping("/send-message-to-user")
  public Mono<ResponseEntity<CommonResponse>> sendMessageToUser(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @RequestBody SendMessageToUserRequest sendMessageRequest) {

    return messagingService.sendMessageToUser(sendMessageRequest, userId, requestId);
  }

  @PostMapping("/send-message-to-conversation")
  public Mono<ResponseEntity<CommonResponse>> sendMessageToConversation(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @RequestBody SendMessageToConversationRequest sendMessageRequest) {

    return messagingService.sendMessageToConversation(sendMessageRequest, userId, requestId);
  }

  @PostMapping("/react-message")
  public Mono<ResponseEntity<CommonResponse>> reactMessage(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @RequestBody ReactMessageRequest reactMessageRequest) {

    CommonResponse data =
        CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();

    return Mono.just(ResponseEntity.ok(data));
  }
}
