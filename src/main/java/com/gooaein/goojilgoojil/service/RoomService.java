package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.response.EndRoomResponseDto;
import com.gooaein.goojilgoojil.dto.response.RoomInOutResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
@Service
public class RoomService {
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
}
