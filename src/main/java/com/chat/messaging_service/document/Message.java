package com.chat.messaging_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Document(collection = "message")
@Data
public class Message {
  @Id
  private String id;

  @Field("sender_id")
  private String senderId;
  @Field("conversation_id")
  private String conversationId;
  @Field("replied_message_id")
  private String repliedMessageId;

  @Field
  private String content;

  @Field("created_at")
  private String createdAt;

  @Field("reaction_tracker")
  private ReactionTracker responseTracker;

  @Data
  public class ReactionTracker {
    private Map<ReactionType, Integer> reactionCount;

    public ReactionTracker() {
      this.reactionCount = new LinkedHashMap<>();
      this.reactionCount.put(ReactionType.LIKE, 0);
      this.reactionCount.put(ReactionType.LOVE, 0);
      this.reactionCount.put(ReactionType.HAHA, 0);
      this.reactionCount.put(ReactionType.WOW, 0);
      this.reactionCount.put(ReactionType.SAD, 0);
      this.reactionCount.put(ReactionType.ANGRY, 0);
    }
  }


  public enum ReactionType {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    SAD,
    ANGRY
  }

}
