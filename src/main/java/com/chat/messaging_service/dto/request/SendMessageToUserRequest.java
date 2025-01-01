package com.chat.messaging_service.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class SendMessageToUserRequest {
  private String temporaryId;
  private String userId;
  private String content;
  private String repliedMessageId;
  private List<String> mentionedMemberIds;
}
