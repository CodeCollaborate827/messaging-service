package com.chat.messaging_service.exception;

import com.chat.messaging_service.dto.response.CommonResponse;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<CommonResponse> handleException(ApplicationException ex) {
    CommonResponse commonErrorResponse = new CommonResponse();
    ErrorCode errorCode = ex.getErrorCode();

    commonErrorResponse.setErrorCode(errorCode);
    commonErrorResponse.setMessage(errorCode.getErrorMessage());
    commonErrorResponse.setRequestId(
        UUID.randomUUID().toString()); // TODO: get it from the request header

    return ResponseEntity.status(errorCode.getHttpStatus()).body(commonErrorResponse);
  }
}
