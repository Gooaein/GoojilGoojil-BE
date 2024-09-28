package com.gooaein.goojilgoojil.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.dto.request.AvatarRequestDto;
import com.gooaein.goojilgoojil.service.GuestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "임시 유저용(수강자) API", description = "임시 유저(수강자) 관련 API. 수강자는 방에 입장할 때 임시로 생성되는 유저입니다.")
public class GuestController {
	private final GuestService guestService;

	/* 임시 User와 그 임시 유저와 연관관계를 맺는 Guest를 생성하고, 임시 User의 정보를 바탕으로 Jwt 토큰을 생성한 뒤 브라우저에 쿠키를 추가한다.
	*  클라이언트로부터 전달받은 아바타 이미지는 Guest에 저장되며, 클라이언트로부터 전달받은 uuid와 맵핑되는 roomId를 찾아 반환한다.
	*  이후 수강자의 모든 Http API 요청은 이 때 발급받은 access_token을 통해 이루어진다. */
	@Operation(summary = "수강자 아바타 생성하기",
			description = "수강자가 질문 방에서 사용할 아바타를 생성합니다." +
					"클라이언트로부터 아바타를 넘겨받아, 이를 바탕으로 임시 유저를 생성하고," +
					"Jwt 토큰을 생성한 뒤 클라이언트에 쿠키를 추가합니다" +
					"또한, 클라이언트로부터 전달받은 uuid와 맵핑되는 roomId를 찾아 반환합니다.")
	@PostMapping("/api/v1/rooms/avatar")
	public ResponseDto<?> createGuestAvatar(HttpServletResponse response,
		@RequestBody AvatarRequestDto avatarRequestDto) {
		return ResponseDto.created(guestService.createAvatar(response, avatarRequestDto.uuid(), avatarRequestDto));
	}
	/* 클라이언트가 입장한 방에 참가한 수강자들을 전체 조회한다. 웹소켓에 접근하기 직전에 호출하여,
	*  웹소켓을 통한 실시간 통신 이전까지의 수강생 현황(실시간으로 입장상태에있는)을 불러와, 질문방에서의 수강생관련 데이터 무결성을 보장한다.*/
	@Operation(summary = "방에 참가한 수강생 전체조회", description = "현재 방에 참여하고 있는 수강생의 id와 avatar를 전체 조회합니다.")
	@GetMapping("/api/v1/users/rooms/{room_id}/guests")
	public ResponseDto<?> getGuests(@PathVariable("room_id") Long roomId) {
		return ResponseDto.ok(guestService.getGuestsByRoomId(roomId));
	}

}
