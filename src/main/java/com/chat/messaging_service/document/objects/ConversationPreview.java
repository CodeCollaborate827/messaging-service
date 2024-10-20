package com.chat.messaging_service.document.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ConversationPreview {

  @JsonProperty("last_message_content")
  private String previewContent;

  @JsonProperty("last_message_time")
  private OffsetDateTime lastUpdated;

  @JsonProperty("preview_type")
  private PreviewType previewType;

//  @JsonProperty("last_message_sender")
//  private String lastMessageSender;

  public enum PreviewType {
    USER_ADDED,
    NEW_MESSAGE,
    CONVERSATION_CREATED
  }
}
