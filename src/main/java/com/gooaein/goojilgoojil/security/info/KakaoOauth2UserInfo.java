package com.gooaein.goojilgoojil.security.info;

import com.gooaein.goojilgoojil.security.info.factory.Oauth2UserInfo;

import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo {
    public KakaoOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }
    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties.get("nickname").toString();
    }
}
