package org.benchmark.bmagent.controller;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
        // Handle the IOException here, for example, by returning a custom error response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}