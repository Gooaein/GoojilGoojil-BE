package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.dto.request.EndRoomRequestDto;
import com.gooaein.goojilgoojil.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RoomController {
    private final RoomService roomService;

    @MessageMapping("/rooms/{roomId}/in")
    @Operation(summary = "방 입장", description = "방에 입장하고 다른 유저들에게 입장을 알립니다.")
    public void enterRoom(
            @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();

        roomService.enterRoom(roomId, sessionId);
    }

    @MessageMapping("/rooms/{roomId}/end")
    @Operation(summary = "방 종료", description = "방을 종료하고 설문 조사 링크와 함께 다른 유저들에게 방 종료를 알립니다.")
    public void endRoom(
            @DestinationVariable String roomId,
            @Payload EndRoomRequestDto endRoomRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();

        roomService.endRoom(sessionId, roomId, endRoomRequestDto.url());
    }
}
