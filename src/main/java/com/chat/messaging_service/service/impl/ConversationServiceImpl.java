package com.chat.messaging_service.service.impl;

import static com.chat.messaging_service.document.Conversation.*;
import static com.chat.messaging_service.document.objects.ConversationPreview.PreviewType;
import static com.chat.messaging_service.utils.Utils.createSuccessResponse;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.dto.request.ConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.request.UpdateConversationRequest;
import com.chat.messaging_service.dto.response.*;
import com.chat.messaging_service.dto.response.ConversationBriefInfoDTO.ConversationPreviewDTO;
import com.chat.messaging_service.event.downstream.conversation.ConversationEvent;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.repository.ConversationRepository;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.service.KafkaProducerService;
import com.chat.messaging_service.service.MediaService;
import com.chat.messaging_service.utils.ConversationUtils;
import com.chat.messaging_service.utils.MessageUtils;
import com.chat.messaging_service.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

  private final ChatUserRepository chatUserRepository;
  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;
  private static final int MAX_MESSAGE_NUM_FETCHED = 20;
  private final KafkaProducerService kafkaProducerService;
  private final MediaService mediaService;
  private final ObjectMapper objectMapper;

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
              return conversationRepository
                  .save(conversation)
                  .doOnNext(
                      savedConversation ->
                          kafkaProducerService.sendNewConversationEventToKafka(
                              ConversationEvent.ConversationEventType.CONVERSATION_NEW,
                              savedConversation))
                  .map(
                      savedConversation -> {
                        chatUserList.forEach(u -> addUserToGroupChat(u, conversation));
                        return chatUserList;
                      })
                  .flatMap(list -> chatUserRepository.saveAll(list).collectList());
            })
        .then(Mono.just(createSuccessResponse("Created group chat successfully", requestId)))
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<Conversation> findDirectConversationBetweenTwoUsers(String userId1, String userId2) {
    if (userId1.equals(userId2)) {
      return conversationRepository.findSelfConversation(userId1);
    }
    return conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
  }

  @Override
  public Mono<Conversation> createNewDirectConversationBetweenTwoUsers(
      ChatUser user1, ChatUser user2) {

    Conversation conversation;
    if (user1.getId().equals(user2.getId())) {
      conversation = ConversationUtils.createSelfConversation(user1);
    } else {
      conversation = ConversationUtils.createDirectConversation(user1, user2);
    }

    return conversationRepository
        .save(conversation)
        .doOnNext(
            savedConversation ->
                kafkaProducerService.sendNewConversationEventToKafka(
                    ConversationEvent.ConversationEventType.CONVERSATION_NEW, savedConversation));
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
  public Mono<ResponseEntity<CommonResponse>> getMessageOfConversation(
      String conversationId,
      String userId,
      String requestId,
      Long fromMessageNo,
      Long toMessageNo) {
    return validateConversationAndUser(conversationId, userId, requestId)
        .flatMap(
            tuple -> {
              Conversation conversation = tuple.getT1();
              ChatUser chatUser = tuple.getT2();

              if (!ConversationUtils.checkUserInConversation(chatUser, conversation)) {
                return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
              }

              Flux<ConversationMessage> messageFlux = null;

              Long from = fromMessageNo;
              Long to = toMessageNo;

              if (to == null) {
                to = conversation.getCurrentMessageNo();
              }

              if (from == null) {
                from = Math.max((to - MAX_MESSAGE_NUM_FETCHED), 0);
              }

              messageFlux =
                  messageRepository.findAllByConversationIdAndMessageNoBetweenInclusive(
                      conversationId, from, to);
              return messageFlux
                  .doOnNext(msg -> log.info("Message from database {}", msg))
                  .collectList()
                  .map(list -> createConversationWithMessagesDTO(conversation, list))
                  .map(
                      conversationWithMsgDto ->
                          Utils.createSuccessResponse(
                              "Get conversation messages successfully",
                              requestId,
                              conversationWithMsgDto));
            })
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> addMemberToConversation(
      String userId,
      String requestId,
      String conversationId,
      ConversationMemberRequest conversationMemberRequest) {
    return validateConversationAndUser(conversationId, userId, requestId)
        .flatMap(tuple -> validateGroupConversationAndMembership(tuple.getT1(), tuple.getT2()))
        .flatMap(
            tuple -> {
              Conversation conversation = tuple.getT1();
              return chatUserRepository
                  .findById(conversationMemberRequest.getMemberId())
                  .switchIfEmpty(
                      Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
                  .flatMap(
                      chatUser ->
                          addMemberToConversation(chatUser, conversation)
                              .then(conversationRepository.save(conversation)))
                  .doOnNext(
                      savedConversation ->
                          kafkaProducerService.sendNewConversationEventToKafka(
                              ConversationEvent.ConversationEventType
                                  .CONVERSATION_GROUP_MEMBER_ADDED,
                              savedConversation))
                  .then(
                      Mono.just(
                          Utils.createSuccessResponse(
                              "Add user to conversation successfully", requestId)))
                  .map(ResponseEntity::ok);
            });
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> removeMemberFromConversation(
      String userId,
      String requestId,
      String conversationId,
      ConversationMemberRequest conversationMemberRequest) {
    return validateConversationAndUser(conversationId, userId, requestId)
        .flatMap(tuple -> validateGroupConversationAndMembership(tuple.getT1(), tuple.getT2()))
        .flatMap(
            tuple -> {
              Conversation conversation = tuple.getT1();
              return chatUserRepository
                  .findById(conversationMemberRequest.getMemberId())
                  .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2)))
                  .flatMap(chatUser -> removeMember(chatUser, conversation))
                  .then(conversationRepository.save(conversation))
                  .doOnNext(
                      savedConversation ->
                          kafkaProducerService.sendNewConversationEventToKafka(
                              ConversationEvent.ConversationEventType
                                  .CONVERSATION_GROUP_MEMBER_REMOVED,
                              savedConversation))
                  .then(
                      Mono.just(
                          Utils.createSuccessResponse(
                              "Remove user to conversation successfully!!", requestId)))
                  .map(ResponseEntity::ok);
            });
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> updateConversationMetaData(
      UpdateConversationRequest updateConversationRequest,
      String conversationId,
      String userId,
      String requestId) {
    return validateAndProcessGroupConversation(
        conversationId,
        userId,
        requestId,
        conversation -> {
          conversation.setGroupConversationName(updateConversationRequest.getConversationName());
          return conversationRepository.save(conversation);
        },
        ConversationEvent.ConversationEventType.CONVERSATION_NAME_UPDATED);
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> updateGroupImage(
      String userId, String requestId, String conversationId, Mono<FilePart> groupImageFilePart) {
    return validateAndProcessGroupConversation(
        conversationId,
        userId,
        requestId,
        conversation ->
            mediaService
                .uploadImage("update_group_image", requestId, groupImageFilePart)
                .mapNotNull(
                    res -> objectMapper.convertValue(res, MediaResource.class).getSecureUrl())
                .flatMap(
                    groupImageUrl -> {
                      conversation.setGroupConversationAvatar(groupImageUrl);
                      return conversationRepository.save(conversation);
                    }),
        ConversationEvent.ConversationEventType.CONVERSATION_IMAGE_UPDATED);
  }

  private Mono<ResponseEntity<CommonResponse>> validateAndProcessGroupConversation(
      String conversationId,
      String userId,
      String requestId,
      Function<Conversation, Mono<Conversation>> processConversation,
      ConversationEvent.ConversationEventType eventType) {

    return validateConversationAndUser(conversationId, userId, requestId)
        .flatMap(
            tuple -> {
              Conversation conversation = tuple.getT1();
              ChatUser currentUser = tuple.getT2();

              if (!ConversationType.GROUP.equals(conversation.getConversationType())) {
                return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR8));
              }

              if (!ConversationUtils.checkUserInConversation(currentUser, conversation)) {
                return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
              }

              return processConversation.apply(conversation);
            })
        .doOnNext(
            updatedConversation ->
                kafkaProducerService.sendNewConversationEventToKafka(
                    eventType, updatedConversation))
        .then(
            Mono.just(
                Utils.createSuccessResponse(
                    "Update metadata conversation successfully!!", requestId)))
        .map(ResponseEntity::ok);
  }

  private Mono<Void> addMemberToConversation(ChatUser chatUser, Conversation conversation) {
    ConversationMember member = ConversationUtils.convertToConversationMember(chatUser);

    if (conversation.getMemberIds().contains(member.getId())) {
      return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR7));
    }

    ConversationUtils.addMemberToConversation(conversation, member);
    conversation.getSeenStatusTracker().updateSeenMessageNo(member.getId(), 0L);
    ConversationPreview conversationPreview =
        ConversationUtils.createConversationPreview(conversation, PreviewType.USER_ADDED);
    conversation.setConversationPreview(conversationPreview);

    return Mono.empty();
  }

  private ConversationWithMessagesDTO createConversationWithMessagesDTO(
      Conversation conversation, List<ConversationMessage> conversationMessages) {
    List<ConversationMessageDTO> conversationMessageDTOS =
        conversationMessages.stream().map(MessageUtils::convertToconversationMessageDTO).toList();

    return ConversationUtils.convertToConversationWithMessageDTO(
        conversation, conversationMessageDTOS);
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

  private Mono<Void> removeMember(ChatUser chatUser, Conversation conversation) {
    ConversationMember member = ConversationUtils.convertToConversationMember(chatUser);
    if (!conversation.getMemberIds().contains(member.getId())) {
      return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
    }

    ConversationUtils.removeMemberToConversation(conversation, member);
    conversation.getSeenStatusTracker().removeSeenMessageNo(member.getId());
    return Mono.empty();
  }

  private Mono<Tuple2<Conversation, ChatUser>> validateConversationAndUser(
      String conversationId, String userId, String requestId) {
    return validateConversation(conversationId, requestId)
        .zipWith(validateChatUser(userId, requestId));
  }

  private Mono<Conversation> validateConversation(String conversationId, String requestId) {
    return conversationRepository
        .findById(conversationId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR4, requestId)));
  }

  private Mono<ChatUser> validateChatUser(String userId, String requestId) {
    return chatUserRepository
        .findById(userId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)));
  }

  private Mono<Tuple2<Conversation, ChatUser>> validateGroupConversationAndMembership(
      Conversation conversation, ChatUser currentUser) {
    if (!ConversationType.GROUP.equals(conversation.getConversationType())) {
      return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR6));
    }

    if (!ConversationUtils.checkUserInConversation(currentUser, conversation)) {
      return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5));
    }

    return Mono.just(Tuples.of(conversation, currentUser));
  }
}
