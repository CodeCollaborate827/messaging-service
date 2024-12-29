package com.chat.messaging_service.document;

import com.chat.messaging_service.document.objects.ReactionTracker;
import java.time.Instant;
import java.util.List;
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

  // TODO: this should be a ref or something, because later you need to fetch the repliedMessage
  // along with the message.
  @Field("replied_message_id")
  private String repliedMessageId;

  @Field private String content;
  @Field private Long messageNo;

  private List<String> mentionedMemberIds;

  @Field("created_at")
  @Builder.Default
  private Long createdAt = Instant.now().getEpochSecond();

  @Field("reaction_tracker")
  @Builder.Default
  private ReactionTracker reactionTracker = new ReactionTracker();
}
