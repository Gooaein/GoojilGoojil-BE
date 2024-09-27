package com.gooaein.goojilgoojil.intercepter.pre;

import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.security.info.JwtUserInfo;
import com.gooaein.goojilgoojil.security.provider.JwtAuthenticationManager;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
//      String authorizationHeaders = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String authorizationURI = request.getURI().getQuery();
        String authorizationHeaders = authorizationURI.split("token=")[1];
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String token = authorizationHeaders.substring(7); // "Bearer " 제거
            Claims claims = null;
            try {
                claims = jwtUtil.validateToken(token);

                if (jwtUtil.isRefreshToken(claims)) {
                    throw new CommonException(ErrorCode.TOKEN_TYPE_ERROR);
                }

            } catch (ExpiredJwtException e) {
                log.error("Token has expired", e);
                throw new CommonException(ErrorCode.EXPIRED_TOKEN_ERROR);
            } catch (MalformedJwtException e) {
                log.error("Malformed token", e);
                throw new CommonException(ErrorCode.TOKEN_MALFORMED_ERROR);
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported token", e);
                throw new CommonException(ErrorCode.TOKEN_UNSUPPORTED_ERROR);
            } catch (JwtException e) {
                log.error("JWT exception", e);
                throw new CommonException(ErrorCode.TOKEN_UNKNOWN_ERROR);
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
