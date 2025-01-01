package com.chat.messaging_service.event.downstream;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// this is the wrapper for all message events (MESSAGE_NEW, MESSAGE_DELIVERED, MESSAGE_SENT,
// MESSAGE_REACTED, MESSAGE_EDITED, MESSAGE_DELETED)
public class MessageEvent {
  public enum MessageEventType {
    MESSAGE_NEW,
    MESSAGE_DELIVERED,
    MESSAGE_SENT,
    MESSAGE_REACTED,
    MESSAGE_EDITED,
    MESSAGE_DELETED
  }

  private MessageEventType messageType;
  private String messageId;
  private String conversationId;
  private Object data; // depends on the messageType the data would be different
  private List<String> conversationMemberIds;
}
