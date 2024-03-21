package org.benchmarker.bmcontroller.common.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalErrorResponse;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class GlobalExceptionTest {
    @Test
    @DisplayName("GlobalErrorResponse 생성자 테스트")
    void test1() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        // when
        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.builder()
            .status(errorCode.getHttpStatus())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .build();

        // then
        assertThat(globalErrorResponse.getCode()).isEqualTo(errorCode.name());
        assertThat(globalErrorResponse.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(globalErrorResponse.getStatus()).isEqualTo(errorCode.getHttpStatus());
    }
    @Test
    @DisplayName("GlobalErrorResponse toResponseEntity(ErrorCode errorCode) 테스트")
    void test2() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        
        // when
        ResponseEntity<GlobalErrorResponse> responseEntity = GlobalErrorResponse.toResponseEntity(
            errorCode);
        GlobalErrorResponse globalErrorResponse = responseEntity.getBody();

        // then
        assertThat(globalErrorResponse.getCode()).isEqualTo(errorCode.name());
        assertThat(globalErrorResponse.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(globalErrorResponse.getStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("GlobalException toResponseEntity(GlobalException ex) 테스트")
    void test4() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        // when
        ResponseEntity<GlobalErrorResponse> responseEntity = GlobalErrorResponse.toResponseEntity(
            new GlobalException(errorCode));
        GlobalErrorResponse globalErrorResponse = responseEntity.getBody();

        // then
        assertThat(globalErrorResponse.getCode()).isEqualTo(errorCode.name());
        assertThat(globalErrorResponse.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(globalErrorResponse.getStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("GlobalException toErrorCode 테스트")
    void test5() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        GlobalErrorResponse globalErrorResponse = GlobalErrorResponse.builder()
            .status(errorCode.getHttpStatus())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .build();

        // when
        ErrorCode findErrorCode = GlobalException.toErrorCode(new GlobalException(errorCode));
        try{
            throw new GlobalException(globalErrorResponse);
        }catch (GlobalException e){
            assertThat(e.getErrorCode()).isEqualTo(errorCode);
        }


        // then
        assertThat(findErrorCode).isEqualTo(errorCode);
    }

    @Test
    @DisplayName("GlobalException 생성자 테스트")
    void testConstructorWithErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        // when & then
        ErrorCode getCode = assertThrows(GlobalException.class,
            () -> {throw new GlobalException(errorCode);})
            .getErrorCode();

        assertThat(getCode).isEqualTo(errorCode);
        assertThat(getCode.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

}