package com.gooaein.goojilgoojil.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.gooaein.goojilgoojil.security.info.AuthenticationResponse;
import com.gooaein.goojilgoojil.utility.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.request.AvatarRequestDto;
import com.gooaein.goojilgoojil.dto.response.GuestResponseDto;
import com.gooaein.goojilgoojil.dto.response.JwtTokenDto;
import com.gooaein.goojilgoojil.dto.type.EProvider;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.RoomRepository;
import com.gooaein.goojilgoojil.repository.UserRepository;
import com.gooaein.goojilgoojil.utility.JwtUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestService {
	private final GuestRepository guestRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;
	private final JwtUtil jwtUtil;

	@Transactional
	public void createAvatar(
			HttpServletResponse response,
			Long roomId,
			AvatarRequestDto avatarRequestDto) throws IOException {

		User guestUser = saveUser();

		JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(guestUser.getId(), ERole.GUEST);
		guestUser.updateRefreshToken(jwtTokenDto.refreshToken());

		Room room = getRoomByRoomId(roomId);

		Guest guest = Guest.builder()
			.user(guestUser)
			.room(room)
			.avatarBase64(avatarRequestDto.avatarBase64())
			.build();

		guestRepository.save(guest);
		AuthenticationResponse.makeLoginSuccessResponse(response, jwtTokenDto, jwtUtil.getRefreshExpiration());
		response.sendRedirect("https://goojilgoojil.com/");
	}

	// 임시 유저 저장
	private User saveUser() {

		String uuid = UUID.randomUUID().toString();
		String nickname = "quest" + uuid.substring(0, 8);
		User user = User.builder()
			.serialId(uuid)
			.provider(EProvider.DEFAULT)
			.role(ERole.GUEST)
			.nickname(nickname)
			.password(uuid)
			.build();

		userRepository.saveAndFlush(user);

		return user;
	}

	// 방 아이디로 방 조회
	private Room getRoomByRoomId(Long roomId) {
		return roomRepository.findById(roomId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ROOM));
	}

	@Transactional(readOnly = true)
	public List<GuestResponseDto> getGuestsByRoomId(Long roomId) {

		Room room = getRoomByRoomId(roomId);

		List<GuestResponseDto> guest = guestRepository.findGuestsByRoom(room);

		return guest;
	}
}
