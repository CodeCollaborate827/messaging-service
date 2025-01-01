package com.chat.messaging_service.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "chat_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {
  @Id private String id;
  private String username;

  private String displayName;

  private String avatar;

  private List<String> conversationIds;
}
