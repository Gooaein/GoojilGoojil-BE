package com.gooaein.goojilgoojil.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @Column(name = "guest_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "guest_id")
    private User user;

    @JoinColumn(name = "room_id")
    @ManyToOne
    private Room room;

    @Column(name = "avartar_base64")
    private String avartarBase64;

    @Builder
    public Guest(User user, Room room, String avartarBase64) {
        this.user = user;
        this.room = room;
        this.avartarBase64 = avartarBase64;
    }
}
