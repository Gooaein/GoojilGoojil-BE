package com.gooaein.goojilgoojil.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AvatarRequestDto(
	@JsonProperty("avatar_base64")
	String avatarBase64
) {
}
