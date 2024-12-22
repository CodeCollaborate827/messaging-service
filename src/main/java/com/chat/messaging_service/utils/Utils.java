package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.dto.response.CommonResponse;
import com.chat.messaging_service.event.upstream.UserRegistrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.Base64;

public class Utils {
  private static ObjectMapper objectMapper =
      JsonMapper.builder()
          // Register JavaTimeModule to handle Java 8 date/time types
          .addModule(new JavaTimeModule())
          .build();

  public static ChatUser convertToChatUser(UserRegistrationEvent userRegistrationEvent) {
    return ChatUser.builder()
        .id(userRegistrationEvent.getUserId())
        .avatar(userRegistrationEvent.getAvatar())
        .displayName(userRegistrationEvent.getDisplayName())
        .username(userRegistrationEvent.getUsername())
        .conversationIds(new ArrayList<>())
        .build();
  }

  public static CommonResponse createSuccessResponse(String message, String requestId) {
    return CommonResponse.builder().message(message).requestId(requestId).build();
  }

  public static CommonResponse createSuccessResponse(
      String message, String requestId, Object data) {
    return CommonResponse.builder().message(message).requestId(requestId).data(data).build();
  }

  public static String encodeBase64(Object object) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(object);
    byte[] encodedBytes = Base64.getEncoder().encode(json.getBytes());
    return new String(encodedBytes);
  }
}
