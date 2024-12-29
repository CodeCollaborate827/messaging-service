package com.chat.messaging_service.service.impl;

import static com.chat.messaging_service.document.objects.ConversationPreview.PreviewType;
import static com.chat.messaging_service.utils.ConversationUtils.addMessageToConversation;
import static com.chat.messaging_service.utils.MessageUtils.createNewMessage;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.document.objects.ReactionTracker;
import com.chat.messaging_service.dto.request.ReactMessageRequest;
import com.chat.messaging_service.dto.request.SendMessageToConversationRequest;
import com.chat.messaging_service.dto.request.SendMessageToUserRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.dto.response.SendMessageToUserResponse;
import com.chat.messaging_service.enums.MessageReaction;
import com.chat.messaging_service.event.downstream.MessageEvent;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ChatUserService;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.service.KafkaProducerService;
import com.chat.messaging_service.service.MessagingService;
import com.chat.messaging_service.utils.ConversationUtils;
import com.chat.messaging_service.utils.EventUtils;
import com.chat.messaging_service.utils.Utils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

  private final MessageRepository messageRepository;
  private final ChatUserService chatUserService;
  private final ConversationService conversationService;
  private final KafkaProducerService kafkaProducerService;

  @Override
  public Mono<ResponseEntity<CommonResponse>> sendMessageToUser(
      SendMessageToUserRequest sendMessageRequest, String userId, String requestId) {
    // TODO: try to refact this code
    return chatUserService
        .findById(sendMessageRequest.getUserId())
        .zipWith(chatUserService.findById(userId))
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
        .flatMap(
            tuple2 ->
                // find conversation between the two users
                conversationService
                    .findDirectConversationBetweenTwoUsers(
                        tuple2.getT1().getId(), tuple2.getT2().getId())
                    // create if not found
                    .switchIfEmpty(
                        conversationService.createNewDirectConversationBetweenTwoUsers(
                            tuple2.getT1(), tuple2.getT2()))
                    .flatMap(
                        conversation -> {
                          log.info("conversation: {}", conversation);
                          ConversationMessage message =
                              createNewMessage(conversation, sendMessageRequest, userId);
                          return this.sendMessageToConversation(conversation, message)
                              // send kafka event for new message
                              .doOnNext(
                                  savedConversation -> {
                                    handleMessageEventNewMessage(savedConversation, message);
                                    checkAndHandleNotificationFoMembersMentionedInMessage(
                                        savedConversation, message);
                                  })
                              .then(
                                  Mono.just(
                                      Utils.createSuccessResponse(
                                          "Sending message...",
                                          requestId,
                                          new SendMessageToUserResponse(conversation.getId()))))
                              .map(ResponseEntity::ok);
                        }));
  }

  private Mono<Conversation> sendMessageToConversation(
      Conversation conversation, ConversationMessage message) {
    // save message
    return messageRepository
        .save(message)
        .flatMap(
            savedMsg -> {
              // add it to conversation as last message
              addMessageToConversation(conversation, savedMsg);
              ConversationPreview conversationPreview =
                  ConversationUtils.createConversationPreview(
                      conversation, PreviewType.NEW_MESSAGE, message);
              conversation.setConversationPreview(conversationPreview);
              return conversationService.save(conversation);
            });
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> sendMessageToConversation(
      SendMessageToConversationRequest sendMessageRequest, String userId, String requestId) {
    return chatUserService
        .findById(userId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2)))
        .zipWith(conversationService.findById(sendMessageRequest.getConversationId()))
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR4)))
        .flatMap(
            tuple2 -> {
              Conversation conversation = tuple2.getT2();
              ConversationMessage message =
                  createNewMessage(conversation, sendMessageRequest, userId);
              return this.sendMessageToConversation(conversation, message)
                  // send kafka event for new message
                  .doOnNext(
                      savedConversation -> {
                        handleMessageEventNewMessage(savedConversation, message);
                        checkAndHandleNotificationFoMembersMentionedInMessage(
                            savedConversation, message);
                      })
                  .then(
                      Mono.just(
                          ResponseEntity.ok(
                              Utils.createSuccessResponse("Sending message...", requestId))));
            });
  }

  private void handleMessageEventNewMessage(
      Conversation conversation, ConversationMessage message) {
    MessageEvent newMessageEvent = EventUtils.buildMessageEventNewMessage(conversation, message);
    kafkaProducerService.sendMessageEvent(newMessageEvent);
  }

  @Override
  public Mono<ResponseEntity<CommonResponse>> reactMessage(
      ReactMessageRequest reactMessageRequest, String userId, String requestId) {
    // TODO: code clean up
    return chatUserService
        .findById(userId)
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR2, requestId)))
        .zipWith(conversationService.findById(reactMessageRequest.getConversationId()))
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR4, requestId)))
        .flatMap(
            tuple2 -> {
              // check if user is in conversation
              ChatUser chatUser = tuple2.getT1();
              Conversation conversation = tuple2.getT2();

              boolean userInConversation =
                  conversation.getMemberIds().stream()
                      .anyMatch(memId -> memId.equals(chatUser.getId()));
              if (!userInConversation) {
                return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR5, requestId));
              }
              return Mono.just(conversation);
            })
        .zipWith(messageRepository.findById(reactMessageRequest.getMessageId()))
        .flatMap(
            tuple2 -> {
              // check if message is in conversation
              Conversation conversation = tuple2.getT1();
              ConversationMessage message = tuple2.getT2();

              boolean messageInConversation =
                  conversation.getId().equals(message.getConversationId());
              if (!messageInConversation) {
                return Mono.error(new ApplicationException(ErrorCode.MESSAGING_ERROR7, requestId));
              }
              return Mono.just(tuple2);
            })
        .flatMap(
            tuple2 -> {
              Conversation conversation = tuple2.getT1();
              ConversationMessage message = tuple2.getT2();

              MessageReaction reaction = reactMessageRequest.getReaction();
              boolean reacted =
                  processMessageReaction(
                      message, userId, reaction); // true for reacted, false for unreacted

              Tuple3<Conversation, ConversationMessage, Boolean> t3 =
                  Tuples.of(conversation, message, reacted);
              return Mono.just(t3);
            })
        .doOnNext(
            tuple3 -> {
              MessageReaction reaction = reactMessageRequest.getReaction();

              ConversationMessage message = tuple3.getT2();
              boolean messageReacted = tuple3.getT3();

              handleMessageEventMessageReaction(
                  userId, reaction,
                  tuple3); // for sending the event to all members in the conversation
              if (messageReacted) {
                checkAndHandleNotificationForMessageReacted(
                    userId, message,
                    reaction); // for sending notification to member whose message is reacted
              }
            })
        .map(
            tuple3 -> {
              boolean messageReacted = tuple3.getT3();
              String responseMessage =
                  messageReacted
                      ? "Reacted message successfully"
                      : "Un-reacted message successfully";
              return ResponseEntity.ok(Utils.createSuccessResponse(responseMessage, requestId));
            });
  }

  private void handleMessageEventMessageReaction(
      String reactionSenderId,
      MessageReaction reaction,
      Tuple3<Conversation, ConversationMessage, Boolean> tuple3) {
    Conversation conversation = tuple3.getT1();
    ConversationMessage message = tuple3.getT2();
    boolean messageReacted = tuple3.getT3();
    // send message events (message reacted / un-reacted)
    MessageEvent event =
        EventUtils.buildMessageEventMessageReaction(
            conversation, message, reactionSenderId, reaction, messageReacted);
    kafkaProducerService.sendMessageEvent(event);
  }

  private boolean processMessageReaction(
      ConversationMessage message, String userId, MessageReaction reaction) {
    // init reaction tracker if not exist
    if (message.getReactionTracker() == null) {
      message.setReactionTracker(new ReactionTracker());
    }
    // check if user already reacted the same reaction for the message

    ReactionTracker reactionTracker = message.getReactionTracker();

    // re-react the same reaction means remove existing reaction of the user
    if (reactionTracker.checkIfUserAlreadyReactedSameReaction(userId, reaction)) {
      reactionTracker.removeExistingReactionOfUser(userId, reaction);
      return false; // unreacted
    } else {
      // if first time react, add record
      reactionTracker.addReactionOfUser(userId, reaction);
      return true; // reacted
    }
  }

  private void checkAndHandleNotificationFoMembersMentionedInMessage(
      Conversation conversation, ConversationMessage message) {
    if (conversation == null || message == null) return;

    List<String> mentionedMemberIds = message.getMentionedMemberIds();
    if (mentionedMemberIds == null || mentionedMemberIds.isEmpty()) return;

    // only process if there is any member is mentioned in the message

    // the mentioned member must be member of that conversation
    for (String memberId : mentionedMemberIds) {
      boolean mentionedMemberInConversation =
          conversation.getMemberIds().stream().anyMatch(memId -> memId.equals(memberId));

      if (!mentionedMemberInConversation) {
        log.warn(
            "Cannot mention member id {} in conversation id {}, because that member is not in the conversation",
            memberId,
            conversation.getId());
      } else {
        // send kafka message to create notification for that user
        kafkaProducerService.sendNotificationTriggerEventForMessageMentioned(message, memberId);
      }
    }
  }

  private void checkAndHandleNotificationForMessageReacted(
      String reactionSenderId, ConversationMessage message, MessageReaction reaction) {
    // no notification if user reacts to his/her own message
    if (reactionSenderId.equals(message.getSenderId())) return;

    kafkaProducerService.sendNotificationTriggerEventForMessageReaction(
        reactionSenderId, message, reaction);
  }
}
