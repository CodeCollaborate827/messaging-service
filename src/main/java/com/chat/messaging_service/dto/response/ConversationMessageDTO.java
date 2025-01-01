package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.document.objects.ReactionTracker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMessageDTO {
  private String messageId;
  private String senderId;
  private String repliedMessageId;
  private Long messageNo;
  private String content;
  private Long createdAt;
  private ReactionTracker reactionTracker;
}
