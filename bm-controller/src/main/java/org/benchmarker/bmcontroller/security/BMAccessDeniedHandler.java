package org.benchmarker.bmcontroller.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BMAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseEntity<GlobalErrorResponse> responseEntity = GlobalErrorResponse.toResponseEntity(
            ErrorCode.FORBIDDEN);
        byte[] responseBodyBytes = responseEntity.getBody().toString()
            .getBytes(StandardCharsets.UTF_8);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentLength(responseBodyBytes.length);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getOutputStream().write(responseBodyBytes);
        response.flushBuffer();
    }
}
