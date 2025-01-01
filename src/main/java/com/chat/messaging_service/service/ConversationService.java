package com.chat.messaging_service.service;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.dto.request.ConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.request.UpdateConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ConversationService {
  Mono<ResponseEntity<CommonResponse>> getAllConversationsOfUser(String userId, String requestId);

  Mono<ResponseEntity<CommonResponse>> createNewGroupConversations(
      CreateGroupConversationRequest createConversationRequest, String userId, String requestId);

  Mono<Conversation> findDirectConversationBetweenTwoUsers(String userId1, String userId2);

  Mono<Conversation> createNewDirectConversationBetweenTwoUsers(ChatUser user1, ChatUser user2);

  Mono<Conversation> save(Conversation conversation);

  Mono<Conversation> findById(String conversationId);

  Mono<ResponseEntity<CommonResponse>> getMessageOfConversation(
      String conversationId, String userId, String requestId, Long fromMessageNo, Long toMessageNo);

  Mono<ResponseEntity<CommonResponse>> addMemberToConversation(
      String userId,
      String requestId,
      String conversationId,
      ConversationMemberRequest conversationMemberRequest);

  Mono<ResponseEntity<CommonResponse>> removeMemberFromConversation(
      String userId,
      String requestId,
      String conversationId,
      ConversationMemberRequest conversationMemberRequest);

  Mono<ResponseEntity<CommonResponse>> updateConversationMetaData(
      UpdateConversationRequest updateConversationRequest,
      String conversationId,
      String userId,
      String requestId);

  Mono<ResponseEntity<CommonResponse>> updateGroupImage(
      String userId, String requestId, String conversationId, Mono<FilePart> groupImageFilePart);
}
