package com.gooaein.goojilgoojil.dto.response;

import lombok.Builder;

@Builder
public record GuestResponseDto(
	Long guestId,
	String avatarBase64
) {
}
