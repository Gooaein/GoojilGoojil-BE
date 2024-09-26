package com.gooaein.goojilgoojil.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooaein.goojilgoojil.domain.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String name;

    @JsonProperty("sub_name")
    private String subName;

    private LocalDateTime date;
    private String location;  // location 필드 추가
    private String url;

    public RoomDto(Long id, String name, String subName, LocalDateTime date, String location, String url) {
        this.id = id;
        this.name = name;
        this.subName = subName;
        this.date = date;
        this.location = location;
        this.url = url;
    }

    public RoomDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    public static RoomDto from(Room room) {
        return new RoomDto(
                room.getId(),
                room.getName(),
                room.getSubName(),
                room.getDate(),
                room.getLocation(),
                room.getUrl()
        );
    }
}