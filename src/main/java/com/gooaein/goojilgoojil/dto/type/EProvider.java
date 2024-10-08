package com.gooaein.goojilgoojil.dto.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EProvider {
    DEFAULT("DEFAULT"),
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    APPLE("APPLE");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
