package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record QuestionResponseDto(
        String type,
        String questionId,
        String title,
        String content,
        byte[] avatarBase64,
        String sendTime,
        Integer likeCount,
        String status
) {
}
