package com.chat.messaging_service.event.downstream.message;

import com.chat.messaging_service.enums.MessageReaction;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// this is data for message event MESSAGE_NEW
public class MessageReactionEventData {
  private String reactionSenderId;
  private MessageReaction reaction;
  @Builder.Default private boolean unReacted = false;
  @Builder.Default private Long timestamp = Instant.now().getEpochSecond();
}
