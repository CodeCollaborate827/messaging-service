package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.ReactMessageRequest;
import com.chat.messaging_service.dto.request.SendMessageRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    @PostMapping("/send-message")
    public Mono<ResponseEntity<CommonResponse>> sendMessage(@RequestHeader String userId,
                                                            @RequestHeader String requestId,
                                                            @RequestBody SendMessageRequest sendMessageRequest
                                                            ) {

        CommonResponse data = CommonResponse.builder()
                .message("Everything is good, please check the log")
                .requestId(requestId)
                .data(null)
                .build();

        return Mono.just(ResponseEntity.ok(data));

    }



    @PostMapping("/react-message")
    public Mono<ResponseEntity<CommonResponse>> reactMessage(@RequestHeader String userId,
                                                            @RequestHeader String requestId,
                                                            @RequestBody ReactMessageRequest reactMessageRequest
                                                            ) {

        CommonResponse data = CommonResponse.builder()
                .message("Everything is good, please check the log")
                .requestId(requestId)
                .data(null)
                .build();

        return Mono.just(ResponseEntity.ok(data));

    }
}
