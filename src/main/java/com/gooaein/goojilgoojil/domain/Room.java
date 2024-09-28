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

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "location", nullable = false)  // location 필드를 추가합니다.
    private String location;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "like_threshold", nullable = false)
    private Integer likeThreshold;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Room(String name, LocalDateTime date, String location, String url, Integer likeThreshold, User user) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.likeThreshold = likeThreshold;
        this.url = url;
        this.user = user;
    }
}