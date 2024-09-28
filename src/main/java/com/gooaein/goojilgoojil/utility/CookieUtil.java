package com.gooaein.goojilgoojil.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("goojilgoojil.com")
                .maxAge(60 * 60 * 24) // 1시간 설정
                .httpOnly(false)
                .sameSite("None")
                .secure(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addSecureCookie(HttpServletResponse response, String name, String value, Integer maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("goojilgoojil.com")
                .maxAge(maxAge)
                .httpOnly(false)
                .sameSite("None")
                .secure(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                ResponseCookie deleteCookie = ResponseCookie.from(name,null)
                        .path("/")
                        .domain("goojilgoojil.com")
                        .maxAge(0)
                        .httpOnly(false)
                        .sameSite("None")
                        .secure(true)
                        .build();
                response.addHeader("Set-Cookie", deleteCookie.toString());
            }
        }
    }
    public static Optional<String> refineCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst().map(Cookie::getValue);
    }
}