package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.AddConversationMemberRequest;
import com.chat.messaging_service.dto.request.CreateConversationRequest;
import com.chat.messaging_service.dto.request.UpdateConversationRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
@Slf4j
public class ConversationController {

  //

  @GetMapping("/conversations")
  public Mono<ResponseEntity<CommonResponse>> getConversations(@Header String userId,
                                                               @Header String requestId,
                                                               @RequestParam Integer pageSize,
                                                               @RequestParam Integer currentPage
                                                               ) {

    log.info("userId :{}", userId);
    log.info("requestId :{}", requestId);
    log.info("pageSize: {}", pageSize);
    log.info("currentPage :{}", currentPage);

    CommonResponse data = CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();
    return Mono.just(ResponseEntity.ok(data));
  }

  @GetMapping("/conversations/{conversationId}")
  public Mono<ResponseEntity<CommonResponse>> getConversationDetails(@Header String userId,
                                                                     @Header String requestId
                                                                     ) {

    CommonResponse data = CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();


    return Mono.just(ResponseEntity.ok(data));
  }


  @PostMapping("/conversations")
  public Mono<ResponseEntity<CommonResponse>> createConversation(@Header String userId,
                                                                 @Header String requestId,
                                                                 @RequestBody CreateConversationRequest createConversationRequest
                                                                 ) {

    CommonResponse data = CommonResponse.builder()
            .message("Everything is good, please check the log")
            .requestId(requestId)
            .data(null)
            .build();


    return Mono.just(ResponseEntity.ok(data));
  }


  @PutMapping("/conversations/{conversationId}")
  public Mono<ResponseEntity<CommonResponse>> updateConversation(@Header String userId,
                                                                 @Header String requestId,
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
  public Mono<ResponseEntity<CommonResponse>> addMemberToConversation(@Header String userId,
                                                                      @Header String requestId,
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
    public Mono<ResponseEntity<CommonResponse>> getConversationMessages(@Header String userId,
                                                                        @Header String requestId,
                                                                        @PathVariable String conversationId,
                                                                        @RequestParam Integer fromMessageNo,
                                                                        @RequestParam Integer toMessageNo,
                                                                        @RequestParam Integer lastMessages
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
