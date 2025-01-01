package com.chat.messaging_service.event.downstream.conversation;

import lombok.Builder;

@Builder
public class ConversationAvatarUpdatedData {
    private String avatarUrl;
    private long timestamp;
    private String updatedBy;
}
