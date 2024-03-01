package org.benchmarker.common.error;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleException(GlobalException e) {
        return GlobalErrorResponse.toResponseEntity(e);
    }

}
