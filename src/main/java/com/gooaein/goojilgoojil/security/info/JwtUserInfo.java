package com.gooaein.goojilgoojil.security.info;

import com.gooaein.goojilgoojil.dto.type.ERole;

public record JwtUserInfo(Long userId, ERole role) {
}
