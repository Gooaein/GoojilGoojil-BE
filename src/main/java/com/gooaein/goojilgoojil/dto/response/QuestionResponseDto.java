package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record QuestionResponseDto(
        String type,
        String questionId,
        String title,
        String content,
        String avatarBase64,
        String sendTime,
        Integer likeCount,
        String status
) {
}
