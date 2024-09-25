package com.gooaein.goojilgoojil.presentation;

import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.security.info.JwtUserInfo;
import com.gooaein.goojilgoojil.service.GuestService;
import com.gooaein.goojilgoojil.service.RoomService;
import com.gooaein.goojilgoojil.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserService userService;
    private final RoomService roomService;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();

        // SecurityContext에서 JwtUserInfo 가져오기
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            userService.updateSessionId(userId, sessionId);
        } else {
            log.error("WebSocket 세션에 인증 정보가 없습니다.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();

        // SecurityContext에서 JwtUserInfo 가져오기
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            if(userService.isGuest(userId)) { // 게스트일 경우 유저 및 게스트 자체를 삭제
                roomService.exitRoom(sessionId);
            }
            else { // 방장일경우 세션만 삭제
                userService.updateSessionId(userId, null);
            }
        } else {
            log.error("WebSocket 세션에 인증 정보가 없습니다.");
        }
    }
}

