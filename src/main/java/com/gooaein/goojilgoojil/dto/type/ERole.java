package com.inglo.giggle.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ERole {
    APPLICANT("APPLICANT", "ROLE_APPLICANT"),
    OWNER("OWNER", "ROLE_OWNER");

    private final String name;
    private final String securityName;

    public static ERole fromName(String name) {
        return Arrays.stream(ERole.values()).
                filter(role -> role.getName().equals(name)).
                findFirst().
                orElseThrow(() -> new IllegalArgumentException("No such role: " + name));
    }
}
