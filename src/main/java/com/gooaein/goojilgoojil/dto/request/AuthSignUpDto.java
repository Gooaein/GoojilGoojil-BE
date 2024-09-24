package com.inglo.giggle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "AuthSignUpDto", description = "회원가입 요청")
public record AuthSignUpDto(
        @JsonProperty("serial_id") @Schema(description = "시리얼 ID", example = "example@example.com")
        @NotNull(message = "serial_id는 null이 될 수 없습니다.")
        @Size(min = 6, max = 254, message = "시리얼 ID는 6~254자리로 입력해주세요.")
        @Pattern(regexp = "^[a-z0-9]{6,20}$", message = "소문자 또는 숫자로 이루어진 6자 이상 20자 미만이어야 합니다.")
        String serialId,
        @JsonProperty("password") @Schema(description = "비밀번호", example = "1234567890Aa!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%]).{10,20}$",
                message = "비밀번호는 소문자 1개 이상, 숫자 1개 이상, 특수문자(!, @, #, %, $) 1개 이상으로 구성된 10~20자리 비밀번호로 입력해주세요.")
        @NotNull(message = "password는 null이 될 수 없습니다.")
        String password,
        @JsonProperty("role") @Schema(description = "역할", example = "APPLICANT")
        @NotNull(message = "role은 null이 될 수 없습니다.")
        String role,
        @JsonProperty("device_token") @Schema(description = "디바이스 토큰", example = "example_token")
        String deviceToken
) {
}
