package com.chat.messaging_service.event.downstream.conversation;

import lombok.Builder;

@Builder
public class ConversationMetaDataUpdatedData {
  private String updatedBy;
  private String conversationName;
  private long timestamp;
}
