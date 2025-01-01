package com.chat.messaging_service.document.objects;

import com.chat.messaging_service.enums.MessageReaction;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ReactionTracker {
  private Map<MessageReaction, List<MessageReactionRecord>> reactions;

  public ReactionTracker() {
    this.reactions = new LinkedHashMap<>();
    //      this.reactionCount.put(ReactionType.LIKE, 0);
    //      this.reactionCount.put(ReactionType.LOVE, 0);
    //      this.reactionCount.put(ReactionType.HAHA, 0);
    //      this.reactionCount.put(ReactionType.WOW, 0);
    //      this.reactionCount.put(ReactionType.SAD, 0);
    //      this.reactionCount.put(ReactionType.ANGRY, 0);
  }

  private List<MessageReactionRecord> getRecordsOfReactionType(MessageReaction reaction) {
    return reactions.getOrDefault(reaction, new ArrayList<>());
  }

  public boolean checkIfUserAlreadyReactedSameReaction(String userId, MessageReaction reaction) {
    return getRecordsOfReactionType(reaction).stream().anyMatch(r -> r.getUserId().equals(userId));
  }

  public void removeExistingReactionOfUser(String userId, MessageReaction reaction) {
    List<MessageReactionRecord> recordsOfReactionType = getRecordsOfReactionType(reaction);
    recordsOfReactionType.removeIf(r -> r.getUserId().equals(userId));

    if (recordsOfReactionType.isEmpty()) {
      reactions.remove(reaction); // remove the reaction type if no user reacted from the message
    } else {
      reactions.put(reaction, recordsOfReactionType);
    }
  }

  public void addReactionOfUser(String userId, MessageReaction reaction) {
    List<MessageReactionRecord> recordsOfReactionType = getRecordsOfReactionType(reaction);
    MessageReactionRecord newRecord =
        MessageReactionRecord.builder()
            .userId(userId)
            .reactedAt(Instant.now().getEpochSecond())
            .build();

    recordsOfReactionType.add(newRecord);

    reactions.putIfAbsent(reaction, recordsOfReactionType);
  }
}
