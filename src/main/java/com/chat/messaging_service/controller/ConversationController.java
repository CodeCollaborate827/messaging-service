package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.ConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.request.UpdateConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
@Slf4j
@RequiredArgsConstructor
public class ConversationController {

  // TODO: refactor, the userid should be the first argument when calling the service layer

  private final ConversationService conversationService;

  @GetMapping("/conversations")
  public Mono<ResponseEntity<CommonResponse>> getConversations(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {

    log.info("userId :{}", userId);
    log.info("requestId :{}", requestId);
    log.info("pageSize: {}", pageSize);
    log.info("currentPage :{}", currentPage);

    return conversationService.getAllConversationsOfUser(userId, requestId);
  }

  @GetMapping("/conversations/{conversationId}")
  public Mono<ResponseEntity<CommonResponse>> getConversationDetails(
      @RequestHeader String userId, @RequestHeader String requestId) {

    CommonResponse data =
        CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();
    // TODO: right now, nothing to do with this endpoint, but later will return the right side bar
    // (images,links, conversation details)
    return Mono.just(ResponseEntity.ok(data));
  }

  @PostMapping("/create-group-conversation")
  public Mono<ResponseEntity<CommonResponse>> createConversation(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @RequestBody CreateGroupConversationRequest createConversationRequest) {
    log.info("userId :{}", userId);
    log.info("requestId :{}", requestId);

    return conversationService.createNewGroupConversations(
        createConversationRequest, userId, requestId);
  }

  @PutMapping("/conversations/{conversationId}/metadata")
  public Mono<ResponseEntity<CommonResponse>> updateConversationMetaData(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @PathVariable String conversationId,
      @RequestBody UpdateConversationRequest updateConversationRequest) {
    return conversationService.updateConversationMetaData(
        updateConversationRequest, conversationId, userId, requestId);
  }

  @PutMapping("/conversations/{conversationId}/groupImage")
  public Mono<ResponseEntity<CommonResponse>> updateConversationImage(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @PathVariable String conversationId,
      @RequestPart("groupImage") Mono<FilePart> groupImageFilePart) {
    return conversationService.updateGroupImage(
        userId, requestId, conversationId, groupImageFilePart);
  }

  @PutMapping("/conversations/{conversationId}/add-member")
  public Mono<ResponseEntity<CommonResponse>> addMemberToConversation(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @PathVariable String conversationId,
      @RequestBody ConversationMemberRequest conversationMemberRequest) {
    return conversationService.addMemberToConversation(
        userId, requestId, conversationId, conversationMemberRequest);
  }

  @PutMapping("conversation/{conversationId}/remove-member")
  public Mono<ResponseEntity<CommonResponse>> removeMemberFromConversation(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @PathVariable String conversationId,
      @RequestBody ConversationMemberRequest conversationMemberRequest) {
    return conversationService.removeMemberFromConversation(
        userId, requestId, conversationId, conversationMemberRequest);
  }

  @GetMapping("/conversations/{conversationId}/messages")
  public Mono<ResponseEntity<CommonResponse>> getConversationMessages(
      @RequestHeader String userId,
      @RequestHeader String requestId,
      @PathVariable String conversationId,
      @RequestParam(required = false) Long fromMessageNo,
      @RequestParam(required = false) Long toMessageNo) {

    log.info("userId :{}", userId);
    log.info("requestId :{}", requestId);

    return conversationService.getMessageOfConversation(
        conversationId, userId, requestId, fromMessageNo, toMessageNo);
  }
}
