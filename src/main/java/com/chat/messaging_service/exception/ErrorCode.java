package com.chat.messaging_service.exception;

public enum ErrorCode {

  GATEWAY_1("Token is invalid", 400),
  KAFKA_MESSAGE_PARSING("Error when parsing kafka message", 500),
  MESSAGING_ERROR1("User already exists", 400),
  MESSAGING_ERROR2("User not found", 404);
  private final String errorMessage;
  private final int httpStatus;

  ErrorCode(String errorMessage, int httpStatus) {
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getHttpStatus() {
    return httpStatus;
  }
}
