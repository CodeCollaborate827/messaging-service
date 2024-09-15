package com.chat.messaging_service.controller;

import com.chat.messaging_service.dto.request.ReactMessageRequest;
import com.chat.messaging_service.dto.request.SendMessageRequest;
import com.chat.messaging_service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    @PostMapping("/send-message")
    public Mono<ResponseEntity<CommonResponse>> sendMessage(@Header String userId,
                                                            @Header String requestId,
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
    public Mono<ResponseEntity<CommonResponse>> reactMessage(@Header String userId,
                                                            @Header String requestId,
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
