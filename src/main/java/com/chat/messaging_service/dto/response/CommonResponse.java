package com.chat.messaging_service.dto.response;

import com.chat.messaging_service.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {
  private ErrorCode errorCode;
  private String message;
  private String requestId;
  private Object data;
}
