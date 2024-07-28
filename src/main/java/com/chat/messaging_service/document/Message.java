package com.chat.messaging_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "message")
@Data
public class Message {
  @Id
  private String id;

  @Field("sender_id")
  private String senderId;
  @Field("conversation_id")
  private String conversationId;
  @Field("replied_message_id")
  private String repliedMessageId;

  @Field
  private String content;

  @Field("created_at")
  private String createdAt;

}
