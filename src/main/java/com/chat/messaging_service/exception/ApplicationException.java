package com.chat.user_service.exception;

public class ApplicationException extends RuntimeException {

  private ErrorCode errorCode;

  public ApplicationException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
