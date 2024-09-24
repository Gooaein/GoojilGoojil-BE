package com.inglo.giggle.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Method Not Allowed Error
    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메소드입니다."),

    // Not Found Error
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    NOT_FOUND_RESOURCE(40400, HttpStatus.NOT_FOUND, "해당 리소스가 존재하지 않습니다."),
    NOT_FOUND_LOGIN_USER(40401, HttpStatus.NOT_FOUND, "로그인한 사용자가 존재하지 않습니다."),
    NOT_FOUND_AUTHORIZATION_HEADER(40401, HttpStatus.NOT_FOUND, "Authorization 헤더가 존재하지 않습니다."),
    NOT_FOUND_USER(40401, HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_APPLICANT(40401, HttpStatus.NOT_FOUND, "해당 APPLICANT가 존재하지 않습니다."),
    NOT_FOUND_OWNER(40401, HttpStatus.NOT_FOUND, "해당 OWNER가 존재하지 않습니다."),
    NOT_FOUND_APPLICANT_FILE(40401, HttpStatus.NOT_FOUND, "해당 사용자 파일이 존재하지 않습니다."),

    // Invalid Argument Error
    MISSING_REQUEST_PARAMETER(40000, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_ARGUMENT(40001, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자입니다."),
    INVALID_PARAMETER_FORMAT(40002, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    INVALID_HEADER_ERROR(40003, HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다."),
    MISSING_REQUEST_HEADER(40004, HttpStatus.BAD_REQUEST, "필수 요청 헤더가 누락되었습니다."),
    BAD_REQUEST_PARAMETER(40005, HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터입니다."),
    BAD_REQUEST_JSON(40006, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),
    SEARCH_SHORT_LENGTH_ERROR(40007, HttpStatus.BAD_REQUEST, "검색어는 2글자 이상이어야 합니다."),
    INVALID_PASSPORT_FILE(40008, HttpStatus.BAD_REQUEST, "유효하지 않은 여권 파일입니다."),
    INVALID_REGISTRATION_FILE(40009, HttpStatus.BAD_REQUEST, "유효하지 않은 외국인 등록증 파일입니다."),
    INVALID_TOPIK_FILE(40010, HttpStatus.BAD_REQUEST, "유효하지 않은 토픽 파일입니다."),
    INVALID_SOCIAL_INTEGRATION_PROGRAM_FILE(40011, HttpStatus.BAD_REQUEST, "유효하지 않은 사회통합프로그램 파일입니다."),
    INVALID_SEJONG_INSTITUTE_FILE(40012, HttpStatus.BAD_REQUEST, "유효하지 않은 세종학당 파일입니다."),
    START_AT_AFTER_END_AT(40013, HttpStatus.BAD_REQUEST, "시작일이 종료일보다 늦을 수 없습니다."),


    // Access Denied Error
    ACCESS_DENIED(40300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_MATCH_AUTH_CODE(40301, HttpStatus.FORBIDDEN, "인증 코드가 일치하지 않습니다."),
    NOT_MATCH_USER(40302, HttpStatus.FORBIDDEN, "해당 사용자가 일치하지 않습니다."),


    // Unauthorized Error
    FAILURE_LOGIN(40100, HttpStatus.UNAUTHORIZED, "잘못된 아이디 또는 비밀번호입니다."),
    EXPIRED_TOKEN_ERROR(40101, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN_ERROR(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED_ERROR(40103, HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR(40104, HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않거나 비어있습니다."),
    TOKEN_UNSUPPORTED_ERROR(40105, HttpStatus.UNAUTHORIZED, "지원하지않는 토큰입니다."),
    TOKEN_GENERATION_ERROR(40106, HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR(40107, HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),

    // Document Error
    NOT_FOUND_DOCUMENT(40600, HttpStatus.NOT_FOUND, "존재하지 않는 document입니다."),
    INVALID_EVENT_TYPE(40601, HttpStatus.BAD_REQUEST, "잘못된 event type입니다."),
    INVALID_MODUSIGN_ERROR(40602, HttpStatus.BAD_REQUEST, "모두싸인 api error"),
    DOCUMENT_DATABASE_ERROR(40703, HttpStatus.BAD_REQUEST, "Document 데이터 저장에 실패했습니다."),

    // Announcement
    NOT_FOUND_ANNOUNCEMENT(40600, HttpStatus.NOT_FOUND, "존재하지 않는 아르바이트입니다."),
    NOT_OWNERS_ANNOUNCEMENT(40601, HttpStatus.NOT_FOUND, "해당 고용주의 아르바이트 공고가 아닙니다."),

    // Apply Error
    NOT_FOUND_APPLY(40700, HttpStatus.NOT_FOUND, "존재하지 않는 apply입니다."),
    APPLY_DATABASE_ERROR(40701, HttpStatus.BAD_REQUEST, "Apply 데이터 저장에 실패했습니다."),
    INVALID_PARTTIME_STEP(40702, HttpStatus.NOT_FOUND, "일치하는 part time comment가 없습니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),
    UPLOAD_FILE_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다.");


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}

