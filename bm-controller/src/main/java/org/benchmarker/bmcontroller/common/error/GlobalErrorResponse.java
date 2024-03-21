package org.benchmarker.bmcontroller.common.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class GlobalErrorResponse implements Serializable {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String code;
    private String message;

    @Builder
    @JsonCreator
    public GlobalErrorResponse(
        @JsonProperty("errorCode") String code,
        @JsonProperty("errorMessage") String message,
        @JsonProperty("status") int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public static ResponseEntity<GlobalErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(GlobalErrorResponse.builder()
                .status(errorCode.getHttpStatus())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build()
            );
    }

    public static ResponseEntity<GlobalErrorResponse> toResponseEntity(GlobalException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(GlobalErrorResponse.builder()
                .status(errorCode.getHttpStatus())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build()
            );

    }
}