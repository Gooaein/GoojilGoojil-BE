package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record EndRoomResponseDto(
        String type,
        String url,
        String sendTime
) {
}
