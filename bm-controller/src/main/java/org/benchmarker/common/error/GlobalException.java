package org.benchmarker.common.error;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public static ErrorCode toErrorCode(Throwable e) {
        GlobalException customException = (GlobalException) e;
        return customException.getErrorCode();
    }

    public GlobalException(GlobalErrorResponse e) {
        this.errorCode = ErrorCode.valueOf(e.getMessage());
    }

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
