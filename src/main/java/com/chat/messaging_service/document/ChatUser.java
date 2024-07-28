package com.chat.messaging_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_user")
@Data
public class ChatUser {
  @Id
  private String id;
  private String username;

  @Field("display_name")
  private String displayName;
  private String avatar;
}
