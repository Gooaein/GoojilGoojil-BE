package com.gooaein.goojilgoojil.security.info;

import com.gooaein.goojilgoojil.security.info.factory.Oauth2UserInfo;

import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo {
    public NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Map<String, String> response = (Map<String, String>)attributes.get("response");
        return response.get("id");
    }
    public String getNickname() {
        Map<String, String> response = (Map<String, String>)attributes.get("response");
        return response.get("nickname");
    }
}
