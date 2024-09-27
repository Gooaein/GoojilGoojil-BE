package com.gooaein.goojilgoojil.dto.response;

import com.gooaein.goojilgoojil.domain.Review;
import com.gooaein.goojilgoojil.domain.Room;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
public class ReviewCreateDto {

    @Min(1)
    @Max(5)
    private Integer type1;

    @Min(1)
    @Max(5)
    private Integer type2;

    @Min(1)
    @Max(5)
    private Integer type3;

    @Min(1)
    @Max(5)
    private Integer type4;

    @Min(1)
    @Max(5)
    private Integer type5;

    @Builder
    public ReviewCreateDto(Integer type1, Integer type2, Integer type3, Integer type4, Integer type5) {
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.type4 = type4;
        this.type5 = type5;
    }

    public Review toEntity(Room room) {
        return Review.builder()
                .type1(type1)
                .type2(type2)
                .type3(type3)
                .type4(type4)
                .type5(type5)
                .room(room)
                .build();
    }
}

