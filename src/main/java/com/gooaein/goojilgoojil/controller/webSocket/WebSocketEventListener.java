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

    /* 웹소켓이 연결되고 나서 발생하는 이벤트.
    *  웹소켓 연결 직후, native header로부터 Authorization의 값인 access_token을 추출한다.
    *  만약 토큰이 비어있거나, 잘못된 토큰이 주어지면 에러 로그를 출력하고 소켓 연결이 끊긴다.
    *  추출한 토큰으로부터 유저 정보인 userId를 가져와, 해당 유저 엔티티의 세션아이디를 웹소켓의 세션아이디로 업데이트한다.
    *  추후 이 세션아이디는, 웹소켓 관련 요청 시 유저와의 맵핑을 위해 사용된다. */
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

    /* 웹소켓 연결이 끊기고 나서 발생하는 이벤트
    *  웹소켓 연결이 끝나고 나면, 연결이 종료된 User를 웹소켓 세션 아이디를 통해 맵핑하여 찾는다.
    *  찾은 User가 Guest(수강자)인지 아닌지(강연자) 구분하고, 수강자라면 Guest와 User를 삭제한다. (임시 유저는 더미로 안남김)
    *  강연자라면, 해당 엔티티의 sessionId만 null로 업데이트한다. (강연자는 소켓이 끊겼다고 삭제되면 안됨) */
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

