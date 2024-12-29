package com.chat.messaging_service.document.objects;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MessageReactionRecord {
  private String userId; // member in the conversation

  @Builder.Default private long reactedAt = Instant.now().getEpochSecond();
}
