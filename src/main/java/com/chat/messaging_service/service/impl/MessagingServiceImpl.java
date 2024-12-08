package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.dto.request.SendMessageToConversationRequest;
import com.chat.messaging_service.dto.request.SendMessageToUserRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.dto.response.SendMessageToUserResponse;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import com.chat.messaging_service.repository.MessageRepository;
import com.chat.messaging_service.service.ChatUserService;
import com.chat.messaging_service.service.ConversationService;
import com.chat.messaging_service.service.KafkaProducerService;
import com.chat.messaging_service.service.MessagingService;
import com.chat.messaging_service.utils.ConversationUtils;
import com.chat.messaging_service.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.chat.messaging_service.document.objects.ConversationPreview.PreviewType;
import static com.chat.messaging_service.utils.ConversationUtils.addMessageToConversation;
import static com.chat.messaging_service.utils.MessageUtils.createNewMessage;

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
                                  savedConversation ->
                                      kafkaProducerService.sendNewMessageEventToKafka(
                                          savedConversation, message))
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
                        kafkaProducerService.sendNewMessageEventToKafka(savedConversation, message);
                      })
                  .then(
                      Mono.just(
                          ResponseEntity.ok(
                              Utils.createSuccessResponse("Sending message...", requestId))));
            });
  }
}
