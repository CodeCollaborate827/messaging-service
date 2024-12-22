package com.chat.messaging_service.dto.request;

import lombok.Data;

@Data
public class SendMessageToConversationRequest {
  private String temporaryId;
  private String conversationId;
  private String content;
  private String repliedMessageId;
}
