package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.dto.request.SendMessageToConversationRequest;
import com.chat.messaging_service.dto.request.SendMessageToUserRequest;

public class MessageUtils {
  public static ConversationMessage createNewMessage(
      Conversation conversation, SendMessageToUserRequest sendMessageRequest, String senderId) {
    String conversationId = conversation.getId();
    return ConversationMessage.builder()
        .temporaryId(sendMessageRequest.getTemporaryId())
        .senderId(senderId)
        .messageNo(conversation.getCurrentMessageNo() + 1)
        .conversationId(conversationId)
        .repliedMessageId(sendMessageRequest.getRepliedMessageId())
        .content(sendMessageRequest.getContent())
        .build();
  }

  public static ConversationMessage createNewMessage(
      Conversation conversation,
      SendMessageToConversationRequest sendMessageRequest,
      String senderId) {
    String conversationId = conversation.getId();
    return ConversationMessage.builder()
        .temporaryId(sendMessageRequest.getTemporaryId())
        .senderId(senderId)
        .messageNo(conversation.getCurrentMessageNo() + 1)
        .conversationId(conversationId)
        .repliedMessageId(sendMessageRequest.getRepliedMessageId())
        .content(sendMessageRequest.getContent())
        .build();
  }
}
