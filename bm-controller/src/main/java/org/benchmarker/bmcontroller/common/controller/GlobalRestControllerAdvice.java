package org.benchmarker.bmcontroller.common.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalErrorResponse;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestControllerAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleGlobalException(GlobalException e) {
        return GlobalErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalErrorResponse> handlerResponseEntity(AccessDeniedException e) {
        return GlobalErrorResponse.toResponseEntity(ErrorCode.FORBIDDEN);
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class,
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class,
    })
    public ResponseEntity<GlobalErrorResponse> handleBadRequestException(Exception e) {
        log.error(e.getMessage());
        return GlobalErrorResponse.toResponseEntity(ErrorCode.BAD_REQUEST);
    }
}
