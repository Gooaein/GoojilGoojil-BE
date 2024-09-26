package com.gooaein.goojilgoojil.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "room_name", nullable = false)
    private String name;

    @Column(name = "sub_name", nullable = false)
    private String subName;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "location", nullable = false)  // location 필드를 추가합니다.
    private String location;

    @Column(name = "url", nullable = false)
    private String url;

    @Builder
    public Room(String name, String subName, LocalDateTime date, String location, String url) {
        this.name = name;
        this.subName = subName;
        this.date = date;
        this.location = location;  // location을 생성자에 추가
        this.url = url;
    }
}