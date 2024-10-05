package com.chat.messaging_service.document.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ConversationPreview {
  @JsonProperty("last_message_content")
  private String lastMessageContent;

  @JsonProperty("last_message_time")
  private OffsetDateTime lastMessageTime;

  @JsonProperty("last_message_sender")
  private String lastMessageSender;
}
