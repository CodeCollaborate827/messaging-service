package com.chat.messaging_service.document.objects;

import com.chat.messaging_service.document.ConversationMessage;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ReactionTracker {
  private Map<ConversationMessage.ReactionType, Integer> reactions;

  public ReactionTracker() {
    this.reactions = new LinkedHashMap<>();
    //      this.reactionCount.put(ReactionType.LIKE, 0);
    //      this.reactionCount.put(ReactionType.LOVE, 0);
    //      this.reactionCount.put(ReactionType.HAHA, 0);
    //      this.reactionCount.put(ReactionType.WOW, 0);
    //      this.reactionCount.put(ReactionType.SAD, 0);
    //      this.reactionCount.put(ReactionType.ANGRY, 0);
  }
}
