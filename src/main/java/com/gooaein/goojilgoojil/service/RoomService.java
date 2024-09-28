package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Like;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.request.RoomDto;
import com.gooaein.goojilgoojil.dto.response.EndRoomResponseDto;
import com.gooaein.goojilgoojil.dto.response.RoomInOutResponseDto;
import com.gooaein.goojilgoojil.dto.response.UUIDDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.LikeRepository;
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
    private final LikeRepository likeRepository;

    public List<RoomDto> getAllRooms(Long userId) {
        List<Room> rooms = roomRepository.findAllByUserId(userId);

        return rooms.stream()
                .map(RoomDto::from)
                .collect(Collectors.toList());
    }

    public RoomDto getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ROOM));

        return RoomDto.from(room);
    }
    
    @Transactional
    public void enterRoom(String roomId, String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if(!guest.isIn()) { // 방에 처음 들어온 경우에만 처리
            guest.enterRoom();
            RoomInOutResponseDto responseDto = RoomInOutResponseDto.builder()
                    .type("in")
                    .guestId(guest.getId().toString())
                    .avatarBase64(guest.getAvatarBase64())
                    .sendTime(OffsetDateTime.now().toString())
                    .build();

            messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
        }
    }
    @Transactional
    public void exitRoom(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        RoomInOutResponseDto responseDto = RoomInOutResponseDto.builder()
                .type("out")
                .guestId(guest.getId().toString())
                .avatarBase64(guest.getAvatarBase64())
                .sendTime(OffsetDateTime.now().toString())
                .build();
        messagingTemplate.convertAndSend("/subscribe/rooms/" + guest.getRoom().getId(), responseDto);
        likeRepository.findAllByUserId(userId).forEach(Like::updateUserByQuit);
        guestRepository.delete(guest);
        userRepository.delete(user);
    }

    public void endRoom(String sessionId, String roomId, String url) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (guestRepository.findById(user.getId()).isPresent())
            throw new CommonException(ErrorCode.CANNOT_END_ROOM);
        EndRoomResponseDto responseDto = EndRoomResponseDto.builder()
                .type("end")
                .url(url)
                .sendTime(OffsetDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }
    @Transactional
    public UUIDDto createRoom(Long userId, RoomDto roomDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        String generatedUrl = generateRandomString(12);

        Room room = Room.builder()
                .name(roomDto.getName())
                .date(roomDto.getDate())
                .location(roomDto.getLocation())
                .likeThreshold(roomDto.getLikeThreshold())
                .url(generatedUrl)
                .user(user)
                .build();

        Room savedRoom = roomRepository.save(room);

        return UUIDDto.builder()
                .uuid(savedRoom.getUrl())
                .build();
    }

    // 랜덤 문자열 생성 메소드
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }

}
