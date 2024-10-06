package com.chat.messaging_service.event.downstream;

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
public class NewMessageEvent {
  private String messageId;
  private String repliedMessageId;
  private String conversationId;
  private String messageContent;
  private OffsetDateTime messageCreatedAt;
  private List<String> conversationMemberIds;
}
