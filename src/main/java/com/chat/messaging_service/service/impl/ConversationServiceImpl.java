package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.dto.response.ConversationDTO;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.repository.ConversationRepository;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.utils.ConversationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ChatUserRepository chatUserRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;




    @Override
    public Mono<ResponseEntity<CommonResponse>> getAllConversationsOfUser(String userId, String requestId) {
      log.info("Getting conversations of user: {}", userId);
      return chatUserRepository.findById(userId)
              .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2)))
              .flatMap(chatUser -> {
                  List<String> conversationIds = chatUser.getConversationIds();

                  return conversationRepository
                          .findAllById(conversationIds)
                          .map(conversation -> convertToConversationDto(conversation, userId))
                          .collectList()
                          .map(conversationList -> {
                              CommonResponse response = CommonResponse.builder()
                                      .message("Get conversation successfully")
                                      .requestId(requestId)
                                      .data(conversationList)
                                      .build();

                              return ResponseEntity.ok(response);
                          });
              });

    }

    private ConversationDTO convertToConversationDto(Conversation conversation, String currentUserId) {
        return ConversationDTO.builder()
                .conversationAvatar(conversation.getGroupConversationAvatar())
                .conversationName(ConversationUtils.constructConversationName(conversation, currentUserId))
                .messagePreview(ConversationUtils.getMessagePreview(conversation, currentUserId))
                .isSeen(ConversationUtils.checkIsSeen(conversation, currentUserId))
                .updatedAt(ConversationUtils.getUpdateAtTime(conversation))
                .build();
    }
}
