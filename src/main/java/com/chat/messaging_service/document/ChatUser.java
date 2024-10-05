package com.chat.messaging_service.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "chat_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {
  @Id
  private String id;
  private String username;

  @Field("display_name")
  private String displayName;
  private String avatar;
  @Field("conversation_ids")
  private List<String> conversationIds;
}
