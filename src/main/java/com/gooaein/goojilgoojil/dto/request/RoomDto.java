package com.gooaein.goojilgoojil.dto.request;

import com.gooaein.goojilgoojil.domain.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String name;
    private String subName;
    private LocalDateTime date;
    private String location;  // location 필드 추가
    private String url;

    public RoomDto(String name, String subName, LocalDateTime date, String location) {
        this.name = name;
        this.subName = subName;
        this.date = date;
        this.location = location;
    }

    public RoomDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    public static RoomDto from(Room room) {
        RoomDto dto = new RoomDto();
        dto.id = room.getId();
        dto.name = room.getName();
        dto.subName = room.getSubName();
        dto.date = room.getDate();
        dto.location = room.getLocation();
        dto.url = room.getUrl();
        return dto;
    }
}