package com.gooaein.goojilgoojil.domain;

import com.gooaein.goojilgoojil.dto.response.ReviewDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA를 위해 기본 생성자 필요
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(name = "type1", nullable = false)
    private Integer type1;  // 사용자가 입력한 정수 값 (1~5)

    @Column(name = "type2", nullable = false)
    private Integer type2;  // 사용자가 입력한 정수 값 (1~5)

    @Column(name = "type3", nullable = false)
    private Integer type3;  // 사용자가 입력한 정수 값 (1~5)

    @Column(name = "type4", nullable = false)
    private Integer type4;  // 사용자가 입력한 정수 값 (1~5)

    @Column(name = "type5", nullable = false)
    private Integer type5;  // 사용자가 입력한 정수 값 (1~5)

    @JoinColumn(name = "room_id")
    @ManyToOne
    private Room room;

    @Builder
    public Review(Integer type1, Integer type2, Integer type3, Integer type4, Integer type5, Room room) {
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.type4 = type4;
        this.type5 = type5;
        this.room = room;
    }
}