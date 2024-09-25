package com.gooaein.goojilgoojil.intercepter.pre;

import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.security.info.JwtUserInfo;
import com.gooaein.goojilgoojil.security.provider.JwtAuthenticationManager;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtAuthenticationManager jwtAuthenticationManager;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
      String authorizationHeaders = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String token = authorizationHeaders.substring(7); // "Bearer " 제거
            Claims claims = null;

            try {
                // Access Token 검증
                claims = jwtUtil.validateToken(token);
            } catch (Exception e) {
                // Access Token이 만료되었을 경우, Refresh Token으로 재발급
                try {
                    String newAccessToken = jwtUtil.generateAccessTokenFromRefreshToken(token); // Refresh Token으로 Access Token 재발급
                    claims = jwtUtil.validateToken(newAccessToken);  // 새로 발급된 Access Token 검증
                } catch (Exception ex) {
                    log.error("토큰 재발급 실패: {}", ex.getMessage());
                    throw new RuntimeException("Invalid Refresh Token");
                }
            }

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
        } else {
            throw new RuntimeException("Authorization 헤더가 존재하지 않습니다.");
        }
    }



    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
