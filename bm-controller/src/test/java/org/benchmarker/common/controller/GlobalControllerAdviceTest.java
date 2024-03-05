package org.benchmarker.common.controller;

import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GlobalControllerAdviceTest {

    @SpyBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockBean
    private UserContext userContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        globalControllerAdvice = new GlobalControllerAdvice(userContext);
    }

    @Test
    @DisplayName("전역 예외 처리 테스트")
    void testHandleGlobalException() {
        // Given
        GlobalException exception = new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);

        // When
        ResponseEntity<GlobalErrorResponse> responseEntity = globalControllerAdvice.handleGlobalException(exception);

        // Then
        assertThat(responseEntity.getBody().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.name());
    }

    @Test
    @DisplayName("전역 Model 추가 테스트")
    void testGlobalAttributes() {
        // Given
        Model model = mock(Model.class);

        // When
        globalControllerAdvice.globalAttributes(model);

        // Then
        assertThat(model.getAttribute("version")).isEqualTo(globalControllerAdvice.getVersion());
        assertThat(model.getAttribute("projectName")).isEqualTo(globalControllerAdvice.getName());
        assertThat(model.getAttribute("projectDesc")).isEqualTo(globalControllerAdvice.getDescription());
        assertThat(model.getAttribute("contributorName")).isEqualTo(globalControllerAdvice.getContactName());
        assertThat(model.getAttribute("contributorEmail")).isEqualTo(globalControllerAdvice.getContactEmail());
    }
}
