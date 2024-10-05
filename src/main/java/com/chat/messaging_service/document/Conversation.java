package com.chat.messaging_service.document;

import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.document.objects.SeenStatusTracker;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {
  @Id private String id;
  // TODO: this should be something relational
  private List<ConversationMember> members;

  @Field("conversation_preview")
  private ConversationPreview conversationPreview;

  @Field("updated_at")
  @Builder.Default
  private OffsetDateTime updatedAt =
      OffsetDateTime.now(); // the updated time is calculated based on the last message time

  @Field("is_group_conversation")
  private boolean isGroupConversation;

  @Field("group_conversation_name")
  private String groupConversationName;

  @Field("group_conversation_avatar")
  private String groupConversationAvatar;

  @Field("current_message_no")
  private long currentMessageNo;

  @Field("seen_tracker")
  @Builder.Default
  private SeenStatusTracker seenStatusTracker = new SeenStatusTracker();

  @Field("created_at")
  @Builder.Default
  private OffsetDateTime createdAt = OffsetDateTime.now();
}
