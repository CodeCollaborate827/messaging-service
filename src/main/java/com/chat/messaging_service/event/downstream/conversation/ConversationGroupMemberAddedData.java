package com.chat.messaging_service.event.downstream.conversation;

import lombok.Builder;

@Builder
public class ConversationGroupMemberAddedData {
  private String addedBy;
  private String addedUserId;
  private long timestamp;
}
