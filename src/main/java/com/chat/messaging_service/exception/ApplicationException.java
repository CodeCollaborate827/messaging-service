package com.chat.messaging_service.exception;

import lombok.Data;

@Data
public class ApplicationException extends RuntimeException {
  private String requestId;
  private ErrorCode errorCode;

  public ApplicationException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public ApplicationException(ErrorCode errorCode, String requestId) {
    this.errorCode = errorCode;
    this.requestId = requestId;
  }
}
