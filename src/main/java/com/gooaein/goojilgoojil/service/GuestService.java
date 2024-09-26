package com.gooaein.goojilgoojil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.dto.response.GuestResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestService {
	private final GuestRepository guestRepository;
	private final RoomRepository roomRepository;

	@Transactional(readOnly = true)
	public List<GuestResponseDto> getGuestsByRoomId(Long roomId) {

		Room room = getRoomByRoomId(roomId);

		List<GuestResponseDto> guest = guestRepository.findGuestsByRoom(room);

		return guest;
	}

	// 방 아이디로 방 조회
	private Room getRoomByRoomId(Long roomId) {
		return roomRepository.findById(roomId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ROOM));
	}
}
