package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.document.objects.ConversationPreview;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationBriefInfoDTO {
  private String conversationId;
  private String conversationName;
  private List<String> conversationAvatar;
  private ConversationPreviewDTO messagePreview;
  private boolean isSeen;
  private OffsetDateTime updatedAt;

  @Data
  public static class ConversationPreviewDTO {
    private String previewContent;
    private OffsetDateTime lastUpdated;
    private ConversationPreview.PreviewType previewType;

    public ConversationPreviewDTO(ConversationPreview conversationPreview) {
      if (conversationPreview != null) {
        this.previewContent = conversationPreview.getPreviewContent();
        this.previewType = conversationPreview.getPreviewType();
        this.lastUpdated = conversationPreview.getLastUpdated();
      }
    }
  }
}
