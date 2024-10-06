package com.chat.messaging_service.dto.request;

import lombok.Data;

@Data
public class SendMessageToUserRequest {
  private String temporaryId;
  private String userId;
  private String content;
  private String repliedMessageId;
}
