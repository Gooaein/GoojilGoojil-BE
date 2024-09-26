package com.gooaein.goojilgoojil.dto.request;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record LikeRequestDto(
        String roomId,
        String questionId,
        Long userId
) implements Serializable {
}
