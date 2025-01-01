package com.chat.messaging_service.dto.request;

import lombok.Data;

@Data
public class ConversationMemberRequest {
  private String memberId;
  private Action action;

  public enum Action {
    ADD,
    REMOVE
  }
}
