package org.benchmarker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BMAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<GlobalErrorResponse> responseEntity = GlobalErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED);
        byte[] responseBodyBytes = responseEntity.getBody().toString().getBytes(StandardCharsets.UTF_8);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentLength(responseBodyBytes.length);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getOutputStream().write(responseBodyBytes);
        response.flushBuffer();
    }
}
