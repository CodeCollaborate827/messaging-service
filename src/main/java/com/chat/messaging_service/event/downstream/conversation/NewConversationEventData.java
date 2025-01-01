package com.chat.messaging_service.event.downstream.conversation;

import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewConversationEventData {
  private String conversationId;
  private Map<String, ConversationMember> memberDetails;
  private ConversationType conversationType;
  private String groupConversationName;
  private String groupConversationAvatar;
  private Long createdAt;
}
