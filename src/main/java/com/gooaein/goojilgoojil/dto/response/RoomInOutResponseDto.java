package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record RoomInOutResponseDto(
        String type,
        String guestId,
        String avatarBase64,
        String sendTime
) {
}