package com.gooaein.goojilgoojil.constants;

import java.util.List;


public class Constants {
    public static String CLAIM_USER_ID = "uuid";
    public static String CLAIM_USER_ROLE = "role";
    public static String ACCESS_COOKIE_NAME = "access_token";
    public static String REFRESH_COOKIE_NAME = "refresh_token";
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTHCODE_PREFIX = "AuthCD ";
    public static String AUTHORIZATION_HEADER = "Authorization";

    public static List<String> NO_NEED_AUTH_URLS = List.of(
            "/oauth2/authorization/kakao",
            "/api/v1/no-auth/**", "api/v1/oauth/login",
            "/api/v1/auth/sign-up",
            "/api/v1/auth/id-duplicate",
            "/api/v1/rooms/avatar",
            "/api/v1/rooms/{roomId}/questions",
            "/api/v1/rooms/{roomId}/guests",
            "/api/v1/rooms/{roomId}/reviews",
            "/api-docs.html",
            "/api-docs/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/ws-connection/**",
            "/ws-connection"
            );
    public static List<String> NO_NEED_FILTER_URLS = List.of(
            "/api/v1/no-filter/**",
            "/api/v1/oauth/login",
            "/api/v1/auth/sign-up",
            "/api/v1/auth/id-duplicate",
            "/api/v1/rooms/avatar",
            "/api/v1/rooms/[^/]+/questions",
            "/api/v1/rooms/[^/]+/guests",
            "/api/v1/rooms/[^/]+/reviews",
            "/api-docs.html",
            "/api-docs/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/ws-connection/**",
            "/ws-connection"
    );
    
    public static List<String> USERS_URLS = List.of(
            "/api/v1/users/**");
}
