package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.dto.request.RoomDto;
import com.gooaein.goojilgoojil.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    // 방 목록 조회
    @GetMapping("")
    public ResponseDto<?> getRooms() {
        return ResponseDto.ok(roomService.getAllRooms());
    }

    // 방 생성
    @PostMapping("")
    public ResponseDto<?> createRoom(@RequestBody RoomDto roomDto) {
        return ResponseDto.ok(roomService.createRoom(roomDto));
    }

    @PutMapping("/{id}")
    public ResponseDto<?> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        return ResponseDto.ok(roomService.updateRoom(id, roomDto));
    }

    @GetMapping("/{room_id}/review")
    public ResponseDto<?> getRoomReview(@PathVariable Long room_id) {
        return ResponseDto.ok(roomService.getRoomReview(room_id));
    }
}
