package com.chat.messaging_service.document.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ConversationMember {
  private String id;

  @JsonProperty("display_name")
  private String displayName;

  private String avatar;
}
