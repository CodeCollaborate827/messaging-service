package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
