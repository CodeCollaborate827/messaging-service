package com.chat.messaging_service.event.downstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactedEvent {
  private String senderId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
  private String reaction;
  private Object data;
}
