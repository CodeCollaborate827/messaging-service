package com.chat.messaging_service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupConversationRequest {
    private String conversationName;
    private List<String> memberIds;
}
