package com.chat.messaging_service.service;

import com.chat.messaging_service.dto.request.SendMessageToConversationRequest;
import com.chat.messaging_service.dto.request.SendMessageToUserRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface MessagingService {
  Mono<ResponseEntity<CommonResponse>> sendMessageToUser(
      SendMessageToUserRequest sendMessageRequest, String userId, String requestId);

  Mono<ResponseEntity<CommonResponse>> sendMessageToConversation(
      SendMessageToConversationRequest sendMessageRequest, String userId, String requestId);
}
