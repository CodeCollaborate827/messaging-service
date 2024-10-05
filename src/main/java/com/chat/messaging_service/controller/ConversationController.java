package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.AddConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateGroupConversationRequest;
import com.chat.messaging_service.dto.request.UpdateConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
@Slf4j
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationService conversationService;

  @GetMapping("/conversations")
  public Mono<ResponseEntity<CommonResponse>> getConversations(@RequestHeader String userId,
                                                               @RequestHeader String requestId,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false, defaultValue = "1") Integer currentPage
                                                               ) {

    log.info("userId :{}", userId);
    log.info("requestId :{}", requestId);
    log.info("pageSize: {}", pageSize);
    log.info("currentPage :{}", currentPage);

    return conversationService.getAllConversationsOfUser(userId, requestId);

  }

  @GetMapping("/conversations/{conversationId}")
  public Mono<ResponseEntity<CommonResponse>> getConversationDetails(@RequestHeader String userId,
                                                                     @RequestHeader String requestId
                                                                     ) {

    CommonResponse data = CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();


    return Mono.just(ResponseEntity.ok(data));
  }


  @PostMapping("/create-group-conversation")
  public Mono<ResponseEntity<CommonResponse>> createConversation(@RequestHeader String userId,
                                                                 @RequestHeader String requestId,
                                                                 @RequestBody CreateGroupConversationRequest createConversationRequest
                                                                 ) {
      log.info("userId :{}", userId);
      log.info("requestId :{}", requestId);


      return conversationService.createNewGroupConversations();
  }


  @PutMapping("/conversations/{conversationId}")
  public Mono<ResponseEntity<CommonResponse>> updateConversation(@RequestHeader String userId,
                                                                 @RequestHeader String requestId,
                                                                 @PathVariable String conversationId,
                                                                 @RequestBody UpdateConversationRequest updateConversationRequest
                                                                 ) {
    CommonResponse data = CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();

    return Mono.just(ResponseEntity.ok(data));
  }

  @PutMapping("/conversations/{conversationId}/add-member")
  public Mono<ResponseEntity<CommonResponse>> addMemberToConversation(@RequestHeader String userId,
                                                                      @RequestHeader String requestId,
                                                                      @PathVariable String conversationId,
                                                                      @RequestBody AddConversationMemberRequest addConversationMemberRequest
                                                                      ) {
      CommonResponse data = CommonResponse.builder()
              .message("Everything is good, please check the log")
              .requestId(requestId)
              .data(null)
              .build();

      return Mono.just(ResponseEntity.ok(data));
  }

  @GetMapping("/conversations/{conversationId}/messages")
    public Mono<ResponseEntity<CommonResponse>> getConversationMessages(@RequestHeader String userId,
                                                                        @RequestHeader String requestId,
                                                                        @PathVariable String conversationId,
                                                                        @RequestParam(required = false) Integer fromMessageNo,
                                                                        @RequestParam(required = false) Integer toMessageNo,
                                                                        @RequestParam(required = false) Integer lastMessages
                                                                        ) {

        log.info("userId :{}", userId);
        log.info("requestId :{}", requestId);

        CommonResponse data = CommonResponse.builder()
                .message("Everything is good, please check the log")
                .requestId(requestId)
                .data(null)
                .build();
        return Mono.just(ResponseEntity.ok(data));
    }
}
