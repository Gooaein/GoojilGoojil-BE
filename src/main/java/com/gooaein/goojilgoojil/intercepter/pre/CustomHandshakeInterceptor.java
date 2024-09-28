package com.gooaein.goojilgoojil.intercepter.pre;

import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.security.info.JwtUserInfo;
import com.gooaein.goojilgoojil.security.provider.JwtAuthenticationManager;
import com.gooaein.goojilgoojil.utility.CookieUtil;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtAuthenticationManager jwtAuthenticationManager;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = CookieUtil.refineCookie(httpServletRequest, Constants.ACCESS_COOKIE_NAME).orElse(null);
        if (token == null) {
            log.error("Access token is missing in the cookie.");
            return false;
        }
        Claims claims = null;
        try {
            claims = jwtUtil.validateToken(token);
        } catch (ExpiredJwtException e) {
            log.error("Token is expired.");
            return false;
        } catch (MalformedJwtException e) {
            log.error("Token is malformed.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Token is unsupported.");
            return false;
        } catch (JwtException e) {
            log.error("Token is invalid.");
            return false;
        }
        try {
            // 인증 정보 생성 및 설정
            JwtUserInfo jwtUserInfo = new JwtUserInfo(
                    claims.get(Constants.CLAIM_USER_ID, Long.class),
                    ERole.valueOf(claims.get(Constants.CLAIM_USER_ROLE, String.class))
            );

            // 인증 받지 않은 토큰을 생성
            UsernamePasswordAuthenticationToken unAuthenticatedToken = new UsernamePasswordAuthenticationToken(
                    jwtUserInfo, null, null
            );

            // 인증 받은 토큰
            UsernamePasswordAuthenticationToken authenticatedToken = (UsernamePasswordAuthenticationToken)
                    jwtAuthenticationManager.authenticate(unAuthenticatedToken);

            // 인증 정보 설정
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticatedToken);
            SecurityContextHolder.setContext(securityContext);

            attributes.put("userId", jwtUserInfo.userId());
            return true;
        } catch (Exception e) {
            log.error("Failed to authenticate the token.");
            return false;
        }
    }



    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
