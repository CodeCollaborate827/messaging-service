package com.chat.messaging_service.document.objects;

import com.chat.messaging_service.document.ConversationMessage;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ReactionTracker {
  private Map<ConversationMessage.ReactionType, Integer> reactionCount;

  public ReactionTracker() {
    this.reactionCount = new LinkedHashMap<>();
    //      this.reactionCount.put(ReactionType.LIKE, 0);
    //      this.reactionCount.put(ReactionType.LOVE, 0);
    //      this.reactionCount.put(ReactionType.HAHA, 0);
    //      this.reactionCount.put(ReactionType.WOW, 0);
    //      this.reactionCount.put(ReactionType.SAD, 0);
    //      this.reactionCount.put(ReactionType.ANGRY, 0);
  }
}
