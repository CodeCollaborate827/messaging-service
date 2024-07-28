package com.chat.user_service.exception;

public enum ErrorCode {

  USER_ERROR1("User not found", 404),
  USER_ERROR2("Two users were already friend", 400),
  USER_ERROR3("Friend request was already sent", 400),
  USER_ERROR4("Friend request not found", 400),
  USER_ERROR5("Friend request was already accepted", 400),
  USER_ERROR6("Friend request was already denied", 400),
  USER_ERROR7("You are not allow to accept or deny this request", 403);

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
