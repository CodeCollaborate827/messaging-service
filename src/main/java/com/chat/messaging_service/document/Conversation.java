package com.chat.messaging_service.document;

import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.document.objects.SeenStatusTracker;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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

  public enum ConversationType {
    SELF,
    DIRECT,
    GROUP
  }

  @Id private String id;

  // TODO: this should be something relational

  @Field("member_ids")
  private List<String> memberIds; // this is used to quickly find the conversation between users

  @Field("member_details")
  private Map<String, ConversationMember> memberDetails;

  @Field("conversation_preview")
  private ConversationPreview conversationPreview;

  @Field("updated_at")
  @Builder.Default
  private Long updatedAt =
      Instant.now()
          .getEpochSecond(); // the updated time is calculated based on the last message time

  @Field("conversation_type")
  private ConversationType conversationType;

  //  @Field("group_conversation_name")
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
  private Long createdAt = Instant.now().getEpochSecond();
}
