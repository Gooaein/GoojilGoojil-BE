package com.inglo.giggle.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailedMessage;

    public CommonException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.detailedMessage = message;
    }

    public CommonException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailedMessage = null;
    }

    @Override
    public String getMessage() {
        return detailedMessage != null ? detailedMessage : errorCode.getMessage();
    }
}
