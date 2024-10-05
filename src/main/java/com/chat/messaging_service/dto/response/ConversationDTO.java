package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.document.objects.ConversationPreview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private String id;
    private String conversationName;
    private List<String> conversationAvatar;
    private ConversationPreviewDTO messagePreview;
    private boolean isSeen;
    private OffsetDateTime updatedAt;

    @Data
    public static class ConversationPreviewDTO {
        private String lastMessageContent;
        private OffsetDateTime lastMessageTime;
        private String lastMessageSender;

        public ConversationPreviewDTO(ConversationPreview conversationPreview) {
            if (conversationPreview != null) {
                this.lastMessageContent = conversationPreview.getLastMessageContent();
                this.lastMessageSender = conversationPreview.getLastMessageSender();
                this.lastMessageTime = conversationPreview.getLastMessageTime();
            }
        }
    }
}
