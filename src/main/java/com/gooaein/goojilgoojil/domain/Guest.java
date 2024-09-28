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

    @Column(name = "avatar_base64", columnDefinition = "LONGBLOB")
    private byte[] avatarBase64;

    @Column(name = "is_in")
    private boolean isIn;

    @Builder
    public Guest(User user, Room room, byte[] avatarBase64) {
        this.user = user;
        this.room = room;
        this.avatarBase64 = avatarBase64;
        this.isIn = false;
    }

    public void enterRoom() {
        this.isIn = true;
    }
}
