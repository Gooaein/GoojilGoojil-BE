package com.gooaein.goojilgoojil.dto.response;

import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.type.EProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "UserDetailDto", description = "유저 상세 정보 조회 Dto")
public record UserDetailDto(
        @Schema(description = "유저 ID", example = "1")
        @NotNull(message = "유저 ID가 없습니다.")
        Long id,
        @Schema(description = "닉네임", example = "홍길동")
        String nickname,
        @Schema(description = "로그인 제공자", example = "KAKAO, GOOGLE, APPLE, DEFAULT")
        @NotNull(message = "로그인 제공자가 없습니다.")
        EProvider provider

) {
        public static UserDetailDto fromEntity(User user) {
            return UserDetailDto.builder()
                    .id(user.getId())
                    .provider(user.getProvider())
                    .nickname(user.getNickname())
                    .build();
        }
}
