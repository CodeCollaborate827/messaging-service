package com.chat.messaging_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private String id;
    private String conversationName;
    private String conversationAvatar;
    private String messagePreview;
    private boolean isSeen;
    private OffsetDateTime updatedAt;
}
