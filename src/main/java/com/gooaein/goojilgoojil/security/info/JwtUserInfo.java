package com.inglo.giggle.security.info;

import com.inglo.giggle.dto.type.ERole;

public record JwtUserInfo(Long userId, ERole role) {
}
