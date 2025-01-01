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
public class ConversationEvent {
  public enum ConversationEventType {
    CONVERSATION_NEW,
    CONVERSATION_GROUP_MEMBER_ADDED,
    CONVERSATION_GROUP_MEMBER_REMOVED,
    CONVERSATION_NAME_UPDATED,
    CONVERSATION_IMAGE_UPDATED
  }

  private ConversationEventType conversationEventType;
  private String conversationId;
  private List<String> conversationMemberIds;
  private long timestamp;
  private Object data;
}
