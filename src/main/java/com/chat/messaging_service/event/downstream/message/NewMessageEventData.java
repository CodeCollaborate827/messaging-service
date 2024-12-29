package com.chat.messaging_service.event.downstream.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// this is data for message event MESSAGE_NEW
public class NewMessageEventData {
  private String messageId;
  private String repliedMessageId;
  private String messageContent;
  private Long messageCreatedAt;
  private String senderId;
}
