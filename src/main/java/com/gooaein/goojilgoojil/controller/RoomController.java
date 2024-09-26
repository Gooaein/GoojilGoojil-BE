package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.dto.global.ArgumentNotValidExceptionDto;
import com.gooaein.goojilgoojil.dto.request.RoomDto;
import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.dto.request.EndRoomRequestDto;
import com.gooaein.goojilgoojil.dto.response.ReviewCreateDto;
import com.gooaein.goojilgoojil.dto.response.ReviewDto;
import com.gooaein.goojilgoojil.service.ReviewService;
import com.gooaein.goojilgoojil.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    private final ReviewService reviewService;

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

    @GetMapping("")
    public ResponseDto<?> getRooms() {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseDto.ok(rooms);
    }

    @PostMapping("")
    public ResponseDto<?> createRoom(@RequestBody RoomDto roomDto) {
        RoomDto createdRoom = roomService.createRoom(roomDto);  // 제대로 매핑되었는지 확인
        return ResponseDto.ok(createdRoom);
    }

    @GetMapping("/{room_id}/review")
    public ResponseDto<?> getReview(@PathVariable("room_id") Long roomId) {
        ReviewDto reviewDto = reviewService.getReviewByRoomId(roomId);
        return ResponseDto.ok(reviewDto);
    }

    @PostMapping("/{room_id}/review")
    public ResponseDto<?> createReview(
            @PathVariable("room_id") Long roomId,
            @Valid @RequestBody ReviewCreateDto reviewCreateDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // 유효성 검증 오류가 발생했을 때 필드 오류를 직접 처리
            return ResponseDto.fail(new ArgumentNotValidExceptionDto(bindingResult));
        }

        reviewService.createReview(roomId, reviewCreateDto);
        return ResponseDto.ok(null);
    }

}
