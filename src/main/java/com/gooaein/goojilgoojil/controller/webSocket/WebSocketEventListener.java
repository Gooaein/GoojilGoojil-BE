package com.gooaein.goojilgoojil.controller.webSocket;

import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.service.RoomService;
import com.gooaein.goojilgoojil.service.UserService;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserService userService;
    private final RoomService roomService;
    private final JwtUtil jwtUtil;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String token = headerAccessor.getFirstNativeHeader("Authorization");
        Long userId = null;
        if (token == null) {
            log.error("token is null");
            return;
        } else {
            Claims claims = null;
            try {
                String refinedToken = token.replace("Bearer ", "");
                claims = jwtUtil.validateToken(refinedToken);
            } catch (ExpiredJwtException e) {
                log.error("Token is expired.");
                return;
            } catch (MalformedJwtException e) {
                log.error("Token is malformed.");
                return;
            } catch (UnsupportedJwtException e) {
                log.error("Token is unsupported.");
                return;
            } catch (JwtException e) {
                log.error("Token is invalid.");
                return;
            }
            userId = claims.get(Constants.CLAIM_USER_ID, Long.class);
        }
        try {
            if (userId != null) {
                String sessionId = headerAccessor.getSessionId();
                userService.updateSessionId(userId, sessionId);
            } else {
                log.error("WebSocket 세션에 인증 정보가 없습니다.");
            }
        } catch (Exception e) {
            log.error("WebSocket 연결 시 오류 발생. 사용자 ID: {}", userId);
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();
        Long userId = null;
        try {
            userId = userService.readUserBySessionId(sessionId).id();
        } catch (CommonException e) {
            log.error("User not found with session ID: {}", sessionId);
            // 예외를 다시 던지지 않고 로그만 남기고 종료할 수 있도록 변경
            return;
        }

        try {
            if (userId != null) {
                if (userService.isGuest(userId)) { // 게스트일 경우 유저 및 게스트 자체를 삭제
                    roomService.exitRoom(userId);
                } else { // 방장일 경우 세션만 삭제
                    userService.updateSessionId(userId, null);
                }
            } else {
                log.error("User ID is null after retrieving by session ID");
            }
        } catch (Exception e) {
            log.error("Error occurred while handling WebSocket disconnect for user ID: {}", userId);
        }
    }
}

