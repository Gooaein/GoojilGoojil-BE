package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.annotation.UserId;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RoomController {
    private final RoomService roomService;
    private final ReviewService reviewService;

    /*  수강자가 웹소켓을 통해 질문 방에 참여한 모든 사용자들에게 실시간으로 본인이 참여했음을 알린다.
     *  이는, 실시간 활성 사용자 수를 갱신하기 위해 사용되며, 다른 사용자들은 이를 통해 방에 참여한 사용자 수와 그들의 아바타를 확인할 수 있다.
     *  type(웹소켓 응답 데이터의 타입. 이 경우 in), guestId(수강자 아이디), avatarBase64(수강자 아바타), sendTime(입장 시각)으로 이루어진다. */
    @MessageMapping("/rooms/{roomId}/in")
    @Operation(summary = "방 입장", description = "방에 입장하고 다른 유저들에게 입장을 알립니다.")
    public void enterRoom(
            @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();

        roomService.enterRoom(roomId, sessionId);
    }

    /*  강연자가 웹소켓을 통해 질문 방에 참여한 모든 사용자들에게 실시간으로 세미나가 끝났음을 알리고 설문조사 url을 전송한다.
     *  type(웹소켓 응답 데이터의 타입. 이 경우 end), url(설문조사 url), sendTime(종료 시각)으로 이루어진다. */
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

    /*  강연자가 생성한 방 목록을 조회한다. 강연자의 강의실 관리에 사용될 데이터를 제공한다.
    *   방 배열: id(강의실ID), name(방이름), date(강연날짜), location(강연장소), url(강의실 uuid), likeThreshold(명예의전당 등록 공감 수 기준) */
    @GetMapping("/api/v1/users/rooms")
    @Operation(summary = "방 목록 조회", description = "강연자가 생성한 방 목록을 조회합니다.")
    public ResponseDto<?> getRooms(@UserId Long userId) {
        List<RoomDto> rooms = roomService.getAllRooms(userId);
        return ResponseDto.ok(rooms);
    }

    /*  강연자가 특정 방에 대한 상세정보를 조회한다. 해당 강의에 남겨진 질문들과, 수강생들이 남겨준 리뷰점수를 확인할 수 있다.
    *   id(강의실ID), name(방이름), date(강연날짜), location(강연장소), url(강의실 uuid), likeThreshold(명예의전당 등록 공감 수 기준) */
    @GetMapping("/api/v1/users/rooms/{room_id}")
    @Operation(summary = "방 상세 조회", description = "특정 방에 대한 상세 정보를 조회합니다.")
    public ResponseDto<?> getRoom(@PathVariable("room_id") Long roomId) {
        return ResponseDto.ok(roomService.getRoom(roomId));
    }

    /*  강연자가 방을 생성한다.
    *   1. 클라이언트로부터 title(방제목), date(강연날짜), location(강연장소), likeThreshold(특별한 질문으로 간주할 공감 수 기준)을 입력받고
    *   2. UUID를 발급하여 해당 방에 매핑시키고
    *   3. 방을 생성한 뒤
    *   4. 강연자에게 uuid를 반환한다. */
    @PostMapping("/api/v1/users/rooms")
    @Operation(summary = "방 생성", description = "강연자가 방을 생성합니다.")
    public ResponseDto<?> createRoom(@UserId Long userId, @RequestBody RoomDto roomDto) {
        return ResponseDto.created(roomService.createRoom(userId, roomDto));
    }

    /*  강연자가 특정 방에서 진행한 강연에 대한 리뷰와 그 강연에서 발생했던 모든 질문들을 조회한다.
    *   질문 배열 + 각 리뷰 타입의 평균 점수 */
    @GetMapping("/api/v1/users/rooms/{room_id}/reviews")
    @Operation(summary = "리뷰 조회", description = "특정 방에 대한 리뷰를 조회합니다.")
    public ResponseDto<?> getReview(@PathVariable("room_id") Long roomId) {
        ReviewDto reviewDto = reviewService.getReviewByRoomId(roomId);
        return ResponseDto.ok(reviewDto);
    }

    /*  수강자가 강연에 대한 리뷰 점수를 매긴다.
    *   리뷰에는 총 5가지 타입이 있으며, 각 타입에 대해 1점~5점 사이의 점수를 매길 수 있다. */
    @PostMapping("/api/v1/users/rooms/{room_id}/reviews")
    @Operation(summary = "리뷰 생성", description = "특정 방에 대한 리뷰를 생성합니다.")
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
