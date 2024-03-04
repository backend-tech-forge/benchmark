package org.benchmarker.common.controller;

import lombok.RequiredArgsConstructor;

import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.security.BMAccessDeniedHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestControllerAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleGlobalException(GlobalException e) {
        return GlobalErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalErrorResponse> handlerResponseEntity(AccessDeniedException e) {
        return GlobalErrorResponse.toResponseEntity(ErrorCode.FORBIDDEN);
    }
}
