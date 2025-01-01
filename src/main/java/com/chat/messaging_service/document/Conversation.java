package com.chat.messaging_service.document;

import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.document.objects.SeenStatusTracker;
import com.chat.messaging_service.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

  @Id private String id;

  // TODO: this should be something relational

  private List<String> memberIds; // this is used to quickly find the conversation between users

  private Map<String, ConversationMember> memberDetails;

  private ConversationPreview conversationPreview;

  private ConversationType conversationType;

  @Builder.Default
  private Long updatedAt =
      Instant.now()
          .getEpochSecond(); // the updated time is calculated based on the last message time

  private String groupConversationName;

  private String groupConversationAvatar;

  private long currentMessageNo;

  @Builder.Default private SeenStatusTracker seenStatusTracker = new SeenStatusTracker();

  @Builder.Default private Long createdAt = Instant.now().getEpochSecond();
}
