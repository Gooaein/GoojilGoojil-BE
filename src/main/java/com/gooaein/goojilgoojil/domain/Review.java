package com.gooaein.goojilgoojil.domain;

import com.gooaein.goojilgoojil.dto.response.ReviewDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(name = "type1", nullable = false)
    private Integer type1;

    @Column(name = "type2", nullable = false)
    private Integer type2;

    @Column(name = "type3", nullable = false)
    private Integer type3;

    @Column(name = "type4", nullable = false)
    private Integer type4;

    @Column(name = "type5", nullable = false)
    private Integer type5;

    @JoinColumn(name = "room_id")
    @ManyToOne
    private Room room;

    @Builder
    public Review(ReviewDto reviewDto) {
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.type4 = type4;
        this.type5 = type5;
        this.room = room;
    }

}
