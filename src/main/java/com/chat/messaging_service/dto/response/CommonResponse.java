package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.exception.ErrorCode;
import lombok.Data;

@Data
public class CommonResponse {
  private ErrorCode errorCode;
  private String message;
  private String requestId;
  private Object data;
}
