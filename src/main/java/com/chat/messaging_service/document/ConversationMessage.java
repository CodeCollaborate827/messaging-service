package com.chat.messaging_service.document;

import com.chat.messaging_service.document.objects.ReactionTracker;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMessage {
  @Builder.Default @Id private String id = UUID.randomUUID().toString();

  @Transient
  private String temporaryId; // used for processing the message before saving it to the db

  @Field("sender_id")
  private String senderId;

  @Field("conversation_id")
  private String conversationId;

  @Field("replied_message_id")
  private String repliedMessageId;

  @Field private String content;

  @Field("created_at")
  @Builder.Default
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Field("reaction_tracker")
  @Builder.Default
  private ReactionTracker reactionTracker = new ReactionTracker();

  public enum ReactionType {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    SAD,
    ANGRY
  }
}
