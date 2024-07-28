package com.chat.user_service.exception;

import com.chat.user_service.model.CommonErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<CommonErrorResponse> handleException(ApplicationException ex) {
    CommonErrorResponse commonErrorResponse = new CommonErrorResponse();
    ErrorCode errorCode = ex.getErrorCode();

    commonErrorResponse.errorCode(errorCode.name());
    commonErrorResponse.setMessage(errorCode.getErrorMessage());
    commonErrorResponse.setRequestId(UUID.randomUUID().toString()); //TODO: get it from the request header

    return ResponseEntity.status(errorCode.getHttpStatus()).body(commonErrorResponse);
  }
}
