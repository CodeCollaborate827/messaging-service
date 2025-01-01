package com.chat.messaging_service.event.downstream.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// this event will be processed by Notification manager to create notification for member mentioned
// in the message
// MessageMentionedNotificationTriggerEvent is not part of Message events
public class MessageMentionedNotificationTriggerEvent {
  private String messageSenderId;
  private String mentionedMemberId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
}
