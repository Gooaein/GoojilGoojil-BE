package com.gooaein.goojilgoojil.security.info.factory;

import com.gooaein.goojilgoojil.dto.type.EProvider;
import com.gooaein.goojilgoojil.security.info.KakaoOauth2UserInfo;
import com.gooaein.goojilgoojil.security.info.NaverOauth2UserInfo;

import java.util.Map;

public class Oauth2UserInfoFactory {
    public static Oauth2UserInfo getOauth2UserInfo(EProvider provider, Map<String, Object> attributes){
        switch (provider) {
            case KAKAO:
                return new KakaoOauth2UserInfo(attributes);
            case NAVER:
                return new NaverOauth2UserInfo(attributes);
            default:
                throw new IllegalAccessError("잘못된 제공자 입니다.");
        }
    }
}
