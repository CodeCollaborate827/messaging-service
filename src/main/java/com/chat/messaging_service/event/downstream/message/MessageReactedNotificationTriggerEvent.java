package com.chat.messaging_service.event.downstream.message;

import com.chat.messaging_service.enums.MessageReaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// this event will be processed by Notification manager to create notification for user whose
// message is reacted by other members
// MessageReactedNotificationTriggerEvent is not part of Message events
public class MessageReactedNotificationTriggerEvent {
  private String messageSenderId;
  private String reactionSenderId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
  private MessageReaction reaction;
}
