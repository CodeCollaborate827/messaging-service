package com.chat.messaging_service.dto.request;

import com.chat.messaging_service.enums.MessageReaction;
import lombok.Data;

@Data
public class ReactMessageRequest {
  private String conversationId;
  private String messageId;
  private MessageReaction reaction;
}
