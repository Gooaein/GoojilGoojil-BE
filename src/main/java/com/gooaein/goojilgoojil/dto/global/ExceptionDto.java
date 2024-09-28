package com.gooaein.goojilgoojil.dto.global;

import com.gooaein.goojilgoojil.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(name = "ExceptionDto", description = "API 예외 발생 시 응답 DTO")
public class ExceptionDto {
    @Schema(name = "code", description = "에러 코드")
    @NotNull
    private final Integer code;

    @Schema(name = "message", description = "에러 메시지")
    @NotNull private final String message;

    public ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        if(errorCode == null)
            return new ExceptionDto(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ExceptionDto(errorCode);
    }
}

