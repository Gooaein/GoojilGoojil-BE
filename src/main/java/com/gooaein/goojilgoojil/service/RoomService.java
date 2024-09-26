package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.dto.request.RoomDto;
import com.gooaein.goojilgoojil.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> new RoomDto(room.getId(), room.getName(), room.getSubName(), room.getDate(), room.getLocation(), room.getUrl()))
                .collect(Collectors.toList());
    }

    public Room createRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setName(roomDto.getName());
        room.setSubName(roomDto.getSubName());
        room.setDate(roomDto.getDate());
        room.setLocation(roomDto.getLocation());
        room.setUrl(roomDto.getUrl());
        return roomRepository.save(room);
    }

    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 방이 존재하지 않습니다."));

        room.setName(roomDto.getName());
        room.setSubName(roomDto.getSubName());
        room.setDate(roomDto.getDate());
        room.setLocation(roomDto.getLocation());
        room.setUrl(roomDto.getUrl());

        roomRepository.save(room);

        return RoomDto.from(room);
    }

}