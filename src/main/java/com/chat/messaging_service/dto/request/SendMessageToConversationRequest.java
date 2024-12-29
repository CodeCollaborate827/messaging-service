package com.chat.messaging_service.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class SendMessageToConversationRequest {
  private String temporaryId;
  private String conversationId;
  private String content;
  private String repliedMessageId;
  private List<String> mentionedMemberIds;
}
