package com.gooaein.goojilgoojil.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.service.GuestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "손님 API", description = "손님 관련 API")
@RequestMapping("/api/v1/rooms/{room_id}/")
public class GuestController {
	private final GuestService guestService;

	@Operation(summary = "방에 참가한 손님 전체조회", description = "현재 방에 참여하고 있는 손님의 id와 avatar를 전체 조회합니다.")
	@GetMapping("/guests")
	public ResponseDto<?> getGuests(@PathVariable("room_id") Long roomId) {
		return ResponseDto.ok(guestService.getGuestsByRoomId(roomId));
	}

}
