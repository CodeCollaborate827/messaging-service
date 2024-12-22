package com.chat.messaging_service.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class CreateGroupConversationRequest {
  private String conversationName;
  private List<String> memberIds;
}
