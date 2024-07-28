package com.chat.messaging_service.document;

import com.chat.messaging_service.document.ChatUser;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "conversation")
@Data
public class Conversation {
  @Id
  private String id;
  private List<String> members;
  private ConverationPreview preview;



  @Transient
  private OffsetDateTime updatedAt; // the updated time is calculated based on the last message time

  @Field("is_group_conversation")
  private boolean isGroupConversation;
  @Field("group_conversation_name")
  private String groupConversationName;
  @Field("group_conversation_avatar")
  private String groupConversationAvatar;

  @Field("current_message_no")
  private long currentMessageNo;

  @Field("seen_tracker")
  private Map<String, Long> seenTracker;
  @Field("created_at")
  private OffsetDateTime createdAt;
  @Data
  public class ConverationPreview {
    @Field("last_message_content")
    private String lastMessageContent;
    @Field("last_message_time")
    private OffsetDateTime lastMessageTime;
    @Field("last_message_sender")
    private String lastMessageSender;
  }
}
