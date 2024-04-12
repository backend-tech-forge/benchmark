package org.benchmarker.bmcontroller.common.error;

import lombok.Getter;

/**
 * GlobalException is a custom exception class for handling exceptions.
 */
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private String message = null;

    public static ErrorCode toErrorCode(Throwable e) {
        GlobalException customException = (GlobalException) e;
        return customException.getErrorCode();
    }

    public GlobalException(GlobalErrorResponse e) {
        this.errorCode = ErrorCode.valueOf(e.getCode());
    }

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public GlobalException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
