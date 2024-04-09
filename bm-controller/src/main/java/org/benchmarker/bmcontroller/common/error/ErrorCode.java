package org.benchmarker.bmcontroller.common.error;

import lombok.Getter;

/**
 * ErrorCode is a custom enum class for handling error codes.
 * <p>It has the following final fields:
 * <p>String {@link #message}
 * <p>int {@link #httpStatus}
 */
@Getter
public enum ErrorCode {

    /**
     * 400
     */
    BAD_REQUEST(400, "잘못된 요청입니다."),
    ALREADY_RUNNING(400, "해당 테스트를 이미 진행중입니다"),
    INVALID_JSON(400, "잘못된 JSON 형식입니다."),
    PASSWORD_NOT_MATCH(400, "패스워드를 정확히 입력해주세요."),
    USER_NOT_SAME_GROUP(400, "그룹이 다른 사용자입니다."),
    GROUP_ALREADY_EXIST(400, "그룹이 이미 존재합니다."),
    USER_NOT_IN_GROUP(400, "그룹에 속해있지 않은 사용자입니다."),
    USER_ALREADY_IN_GROUP(400, "그룹에 이미 속해있는 사용자입니다."),
    HTTP_METHOD_NOT_FOUND(400, "지원하지 않는 HTTP METHOD 입니다."),

    /**
     * 401
     */
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다."),

    /**
     * 403
     */
    FORBIDDEN(403, "권한이 없는 사용자입니다."),

    /**
     * 404
     */
    USER_NOT_FOUND(404, "유저가 존재하지 않습니다."),
    USER_ALREADY_EXIST(404, "유저가 이미 존재합니다."),
    GROUP_NOT_FOUND(404, "그룹이 존재하지 않습니다."),
    TEMPLATE_NOT_FOUND(404, "탬플릿이 존재하지 않습니다."),
    TEMPLATE_RESULT_NOT_FOUND(404, "탬플릿에 대한 결과가 존재하지 않습니다."),
    TEST_NOT_FOUND(404, "테스트 결과가 존재하지 않습니다."),

    /**
     * 500
     */
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류"),;
  
    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String s) {
        this.httpStatus = httpStatus;
        this.message = s;
    }

}
