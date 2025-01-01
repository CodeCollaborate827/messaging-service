package com.chat.messaging_service.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  GATEWAY_1("Token is invalid", 400),
  KAFKA_MESSAGE_PARSING("Error when parsing kafka message", 500),
  MESSAGING_ERROR1("User already exists", 400),
  MESSAGING_ERROR2("User not found", 404),
  MESSAGING_ERROR3("Invalid argument, direct conversation", 500),
  MESSAGING_ERROR4("Conversation Not Found", 404),
  MESSAGING_ERROR5("User not in the conversation", 400),
  MESSAGING_ERROR6("Cannot add more member to a direct conversation", 400),
  MESSAGING_ERROR7("User already in conversation", 400),
  MESSAGING_ERROR8("Can only change image in group conversation", 400),

  MEDIA_UPLOAD_FAILED("Can not upload the image", 500);

  private final String errorMessage;
  private final int httpStatus;

  ErrorCode(String errorMessage, int httpStatus) {
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }
}
