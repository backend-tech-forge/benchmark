package org.benchmarker.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * 400
     */
    BAD_REQUEST(400, "잘못된 요청입니다"),
    PASSWORD_NOT_MATCH(400, "패스워드 불일치"),

    /**
     * 401
     */
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다"),

    /**
     * 403
     */
    FORBIDDEN(403, "권한이 없는 사용자입니다"),

    /**
     * 404
     */
    USER_NOT_FOUND(404, "유저가 존재하지 않습니다"),
    USER_ALREADY_EXIST(404, "유저가 이미 존재합니다"),
    GROUP_NOT_FOUND(404, "그룹이 존재하지 않습니다"),
    TEMPLATE_NOT_FOUND(404, "탬플릿이 존재하지 않습니다"),

    /**
     * 500
     */
    SERVER_DEFAULT_ERROR(500, "서버 내부오류"),
    ;


    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String s) {
        this.httpStatus = httpStatus;
        this.message = s;
    }
}
