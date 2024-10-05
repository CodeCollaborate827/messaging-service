package com.chat.messaging_service.service.impl;

import static com.chat.messaging_service.utils.Utils.createSuccessResponse;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.dto.response.ConversationDTO;
import com.chat.messaging_service.dto.response.ConversationDTO.ConversationPreviewDTO;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.repository.ConversationRepository;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.utils.ConversationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

  private final ChatUserRepository chatUserRepository;
  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;

  @Override
  public Mono<ResponseEntity<CommonResponse>> getAllConversationsOfUser(
      String userId, String requestId) {
    log.info("Getting conversations of user: {}", userId);
    return chatUserRepository
        .findById(userId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2)))
        .flatMap(
            chatUser -> {
              List<String> conversationIds = chatUser.getConversationIds();

              return conversationRepository
                  .findAllIdByOrderByUpdatedAtDesc(conversationIds)
                  .map(conversation -> convertToConversationDto(conversation, userId))
                  .collectList()
                  .map(
                      conversationList -> {
                        CommonResponse response =
                            CommonResponse.builder()
                                .message("Get conversation successfully")
                                .requestId(requestId)
                                .data(conversationList)
                                .build();

                        return ResponseEntity.ok(response);
                      });
            });
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> createNewGroupConversations(
      CreateGroupConversationRequest createConversationRequest, String userId, String requestId) {
    log.info("Creating group conversation, request id: {}", requestId);
    List<String> memberIds = createConversationRequest.getMemberIds();
    // make sure the current user who creates the group chat will be included in the conversation
    boolean containsCurrentUsrId = memberIds.stream().anyMatch(id -> id.equals(userId));
    if (!containsCurrentUsrId) {
      memberIds.add(userId);
    }

    return chatUserRepository
        .findAllById(memberIds)
        .doOnEach(u -> log.info("u : {}", u))
        .collectList()
        .flatMap(
            chatUserList -> {
              Conversation conversation =
                  ConversationUtils.createGroupConversation(
                      chatUserList, createConversationRequest.getConversationName());
              // save the conversation and update its id for each chat user's conversation list
              return conversationRepository
                  .save(conversation)
                  .map(
                      savedConversation -> {
                        // update the conversation id to each chat user's conversation list
                        chatUserList.forEach(u -> addUserToGroupChat(u, conversation));
                        return chatUserList;
                      })
                  .flatMap(list -> chatUserRepository.saveAll(list).collectList());
            })
        .then(
            Mono.just(
                ResponseEntity.ok(
                    createSuccessResponse("Created group chat successfully", requestId))));
  }

  private void addUserToGroupChat(ChatUser chatUser, Conversation conversation) {
    chatUser.getConversationIds().addFirst(conversation.getId());
  }

  private ConversationDTO convertToConversationDto(
      Conversation conversation, String currentUserId) {
    ConversationPreview messagePreview =
        ConversationUtils.getMessagePreview(conversation, currentUserId);
    ConversationPreviewDTO conversationPreviewDTO =
        (messagePreview != null) ? new ConversationPreviewDTO(messagePreview) : null;

    return ConversationDTO.builder()
        .id(conversation.getId())
        .conversationAvatar(ConversationUtils.getConversationAvatar(conversation, currentUserId))
        .conversationName(ConversationUtils.constructConversationName(conversation, currentUserId))
        .messagePreview(conversationPreviewDTO)
        .isSeen(ConversationUtils.checkIsSeen(conversation, currentUserId))
        .updatedAt(ConversationUtils.getUpdateAtTime(conversation))
        .build();
  }
}
