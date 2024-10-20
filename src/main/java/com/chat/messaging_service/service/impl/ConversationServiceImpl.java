package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.dto.request.AddConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.dto.response.ConversationBriefInfoDTO;
import com.chat.messaging_service.dto.response.ConversationBriefInfoDTO.ConversationPreviewDTO;
import com.chat.messaging_service.dto.response.ConversationMessageDTO;
import com.chat.messaging_service.dto.response.ConversationWithMessagesDTO;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.repository.ConversationRepository;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.utils.ConversationUtils;
import com.chat.messaging_service.utils.MessageUtils;
import com.chat.messaging_service.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.chat.messaging_service.document.objects.ConversationPreview.*;
import static com.chat.messaging_service.utils.Utils.createSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

  private final ChatUserRepository chatUserRepository;
  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;

  private static final int MAX_MESSAGE_NUM_FETCHED = 20;

  @Override
  public Mono<ResponseEntity<CommonResponse>> getAllConversationsOfUser(
      String userId, String requestId) {
    log.info("Getting conversations of user: {}", userId);
    return chatUserRepository
        .findById(userId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
        .flatMap(
            chatUser -> {
              List<String> conversationIds = chatUser.getConversationIds();

              return conversationRepository
                  .findAllIdByOrderByUpdatedAtDesc(conversationIds)
                  .map(conversation -> convertToConversationDto(conversation, userId, requestId))
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
                      //TODO: send kafka message
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
                    createSuccessResponse("Created group chat successfully", requestId)))
            .map(ResponseEntity::ok);
  }

  @Override
  public Mono<Conversation> findDirectConversationBetweenTwoUsers(String userId1, String userId2) {
    return conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
  }

  @Override
  public Mono<Conversation> createNewDirectConversationBetweenTwoUsers(
      ChatUser user1, ChatUser user2) {

    Conversation directConversation = ConversationUtils.createDirectConversation(user1, user2);

      //TODO: send kafka message
    return conversationRepository.save(directConversation);
//            .doOnNext()
  }

  @Override
  public Mono<Conversation> save(Conversation conversation) {
    return conversationRepository.save(conversation);
  }

  @Override
  public Mono<Conversation> findById(String conversationId) {
    return conversationRepository.findById(conversationId);
  }

    @Override
    public Mono<ResponseEntity<CommonResponse>> getMessageOfConversation(String conversationId,
                                                                         String userId,
                                                                         String requestId,
                                                                         Long fromMessageNo,
                                                                         Long toMessageNo
    ) {
        return conversationRepository.findById(conversationId)
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR4, requestId)))
                .zipWith(chatUserRepository.findById(userId))
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
                .flatMap(tuple -> {
                    Conversation conversation = tuple.getT1();
                    ChatUser chatUser = tuple.getT2();

                    if (!checkUserInConversation(chatUser, conversation)) {
                        return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
                    }

                    Flux<ConversationMessage> messageFlux = null;

                    Long from = fromMessageNo;
                    Long to = toMessageNo;

                    if (to == null) {
                        to = conversation.getCurrentMessageNo();
                    }

                    if (from == null) {
                        // it should not be negative
                        from = Math.max((to - MAX_MESSAGE_NUM_FETCHED), 0);
                    }
                    //TODO: update the seen tracker for the current User
                    messageFlux = messageRepository.findAllByConversationIdAndMessageNoBetweenInclusive(conversationId,from, to);
                    return messageFlux
                            .doOnNext(msg -> log.info("Message from database {}",msg))
                            .collectList()
                            .map(list -> createConversationWithMessagesDTO(conversation, list))
                            .map(conversationWithMsgDto -> Utils.createSuccessResponse("Get conversation messages successfully", requestId, conversationWithMsgDto));
                })
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CommonResponse>> addMemberToConversation(String conversationId, String userId, String requestId, AddConversationMemberRequest addConversationMemberRequest) {
       // TODO: refactor findConversationId, and findChatUserByID in service layer
        // check if the chat user and the conversation exist
        return conversationRepository.findById(conversationId)
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR4, requestId)))
                .zipWith(chatUserRepository.findById(userId))
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
                .flatMap(tuple -> {
                    Conversation conversation = tuple.getT1();
                    ChatUser currentUser = tuple.getT2();

                    if (!conversation.isGroupConversation()) {
                        return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR6));

                    }

                    if (!checkUserInConversation(currentUser, conversation)) {
                        return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
                    }

                    // check if the chat user who will be added to conversation exists
                    return chatUserRepository.findById(addConversationMemberRequest.getMemberId())
                            .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
                            .flatMap(chatUser -> {
                                addMemberToConversation(chatUser, conversation);
                                return conversationRepository.save(conversation);
                            })
                            //TODO: send kafka message
                            .then(Mono.just(Utils.createSuccessResponse("Add user to conversation successfully", requestId)))
                            .map(ResponseEntity::ok);
                });
    }

    private void addMemberToConversation(ChatUser chatUser, Conversation conversation) {
        ConversationMember member = ConversationUtils.convertToConversationMember(chatUser);
        conversation.getMembers().add(member);
        conversation.getSeenStatusTracker().updateSeenMessageNo(member.getId(), 0L);

        ConversationPreview conversationPreview = ConversationUtils.createConversationPreview(conversation, PreviewType.USER_ADDED);
        conversation.setConversationPreview(conversationPreview);
    }

    private ConversationWithMessagesDTO createConversationWithMessagesDTO(Conversation conversation, List<ConversationMessage> conversationMessages) {
        List<ConversationMessageDTO> conversationMessageDTOS = conversationMessages.stream().map(MessageUtils::convertToconversationMessageDTO).toList();

        return ConversationUtils.convertToConversationWithMessageDTO(conversation, conversationMessageDTOS);
    }

    private boolean checkUserInConversation(ChatUser chatUser, Conversation conversation) {
        return conversation.getMembers().stream().anyMatch(member -> member.getId().equals(chatUser.getId()));
    }

    private void addUserToGroupChat(ChatUser chatUser, Conversation conversation) {
    chatUser.getConversationIds().addFirst(conversation.getId());
  }

  private ConversationBriefInfoDTO convertToConversationDto(
      Conversation conversation, String currentUserId, String requestId) {
    ConversationPreview messagePreview =
        ConversationUtils.getMessagePreview(conversation, currentUserId);
    ConversationPreviewDTO conversationPreviewDTO =
        (messagePreview != null) ? new ConversationPreviewDTO(messagePreview) : null;

    return ConversationBriefInfoDTO.builder()
        .conversationId(conversation.getId())
        .conversationAvatar(
            ConversationUtils.getConversationAvatar(conversation, currentUserId, requestId))
        .conversationName(
            ConversationUtils.constructConversationName(conversation, currentUserId, requestId))
        .messagePreview(conversationPreviewDTO)
        .isSeen(ConversationUtils.checkIsSeen(conversation, currentUserId))
        .updatedAt(ConversationUtils.getUpdateAtTime(conversation))
        .build();
  }
}
