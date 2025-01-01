package com.chat.messaging_service.event.downstream.conversation;

import lombok.Builder;

@Builder
public class ConversationGroupMemberRemovedData {
    private String removedBy;
    private String removedUserId;
    private long timestamp;
}
