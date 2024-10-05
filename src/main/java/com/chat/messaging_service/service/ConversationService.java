package com.chat.messaging_service.service;

import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ConversationService {
    Mono<ResponseEntity<CommonResponse>> getAllConversationsOfUser(String userId, String requestId);

    Mono<ResponseEntity<CommonResponse>> createNewGroupConversations(CreateGroupConversationRequest createConversationRequest, String userId, String requestId);

}
