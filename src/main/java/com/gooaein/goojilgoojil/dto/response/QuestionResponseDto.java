package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record QuestionResponseDto(
        String type,
        String questionId,
        String title,
        String content,
        String avartarBase64,
        String sendTime,
        String likeCount,
        String status
) {
}
