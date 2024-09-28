package com.gooaein.goojilgoojil.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooaein.goojilgoojil.domain.Room;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String location;
    @JsonProperty("like_threshold")
    private Integer likeThreshold;
    private String url;
    @Builder
    public RoomDto(Long id, String name, LocalDateTime date, String location, String url, Integer likeThreshold) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.likeThreshold = likeThreshold;
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
                room.getDate(),
                room.getLocation(),
                room.getUrl(),
                room.getLikeThreshold()
        );
    }
}