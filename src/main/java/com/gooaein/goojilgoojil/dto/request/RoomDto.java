package com.gooaein.goojilgoojil.dto.request;

import com.gooaein.goojilgoojil.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String name;
    private String subName;
    private LocalDateTime date;
    private String location;
    private String url;

    public static RoomDto from(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setName(room.getName());
        roomDto.setSubName(room.getSubName());
        roomDto.setDate(room.getDate());
        roomDto.setLocation(room.getLocation());
        roomDto.setUrl(room.getUrl());
        return roomDto;
    }
}
