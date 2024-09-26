package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.request.RoomDto;
import com.gooaein.goojilgoojil.dto.response.EndRoomResponseDto;
import com.gooaein.goojilgoojil.dto.response.RoomInOutResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.RoomRepository;
import com.gooaein.goojilgoojil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class RoomService {
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepository roomRepository;

    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                .map(RoomDto::from)
                .collect(Collectors.toList());
    }

    public void enterRoom(String roomId, String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        RoomInOutResponseDto responseDto = RoomInOutResponseDto.builder()
                .type("in")
                .guestId(guest.getId().toString())
                .avartarBase64(guest.getAvartarBase64())
                .sendTime(OffsetDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }
    @Transactional
    public void exitRoom(String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

//        guestRepository.delete(guest); TODO: 테스트 이후로 풀어야되는 주석
//        userRepository.delete(user); TODO: 테스트 이후로 풀어야되는 주석

        RoomInOutResponseDto responseDto = RoomInOutResponseDto.builder()
                .type("out")
                .guestId(guest.getId().toString())
                .avartarBase64(guest.getAvartarBase64())
                .sendTime(OffsetDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + guest.getRoom().getId(), responseDto);
    }

    public void endRoom(String sessionId, String roomId, String url) {
//        User user = userRepository.findBySessionId(sessionId) TODO: 테스트 이후로 풀어야되는 주석
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER)); TODO: 테스트 이후로 풀어야되는 주석
//        if (guestRepository.findById(user.getId()).isPresent()) TODO: 테스트 이후로 풀어야되는 주석
//            throw new CommonException(ErrorCode.CANNOT_END_ROOM); TODO: 테스트 이후로 풀어야되는 주석
        EndRoomResponseDto responseDto = EndRoomResponseDto.builder()
                .type("end")
                .url(url)
                .sendTime(OffsetDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }

    public RoomDto createRoom(RoomDto roomDto) {
        // 랜덤 문자열만 생성
        String generatedUrl = generateRandomString(12);  // 도메인 없이 랜덤 문자열만 생성

        Room room = Room.builder()
                .name(roomDto.getName())
                .subName(roomDto.getSubName())
                .date(roomDto.getDate())
                .location(roomDto.getLocation())
                .url(generatedUrl)  // 랜덤 문자열을 URL로 저장
                .build();

        Room savedRoom = roomRepository.save(room);

        // 생성된 방의 ID와 URL만 반환하는 DTO 생성
        return new RoomDto(savedRoom.getId(), savedRoom.getUrl());
    }

    // 랜덤 문자열 생성 메소드
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }

}
