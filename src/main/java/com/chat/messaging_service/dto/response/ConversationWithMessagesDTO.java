package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.SeenStatusTracker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationWithMessagesDTO {
    private String conversationId;
    private Long conversationCurrentMessageNo;
    private List<ConversationMember> members;
    private SeenStatusTracker seenStatusTracker;
    private List<ConversationMessageDTO> messages;
}
