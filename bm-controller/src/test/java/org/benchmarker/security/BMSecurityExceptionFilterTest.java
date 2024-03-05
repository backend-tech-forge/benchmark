package org.benchmarker.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import java.io.IOException;

import org.benchmarker.common.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@SpringBootTest
public class BMSecurityExceptionFilterTest {

    @InjectMocks
    private BMAuthenticationEntryPoint authenticationEntryPoint;

    @InjectMocks
    private BMAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("토큰이 유효하지 않은 경우 401 에러 반환성공")
    public void testCommence() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Unauthorized") {}; // Simulate authentication error

        // when
        authenticationEntryPoint.commence(request, response, authException);

        // then
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).contains(ErrorCode.UNAUTHORIZED.name());
        assertThat(response.getContentAsString()).contains(ErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("토큰이 유효하지 않은 경우 403 에러 반환성공")
    public void testHandle() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException authException = new AccessDeniedException("Forbidden"); // Simulate access denied error

        // when
        accessDeniedHandler.handle(request, response, authException);

        // then
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentAsString()).contains(ErrorCode.FORBIDDEN.name());
        assertThat(response.getContentAsString()).contains(ErrorCode.FORBIDDEN.getMessage());
    }
}