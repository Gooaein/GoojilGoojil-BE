package com.gooaein.goojilgoojil.dto.response;

import com.gooaein.goojilgoojil.domain.Review;
import com.gooaein.goojilgoojil.domain.Room;
import lombok.Builder;

@Builder
public record ReviewDto(
        Long id,
        Integer type1,
        Integer type2,
        Integer type3,
        Integer type4,
        Integer type5,
        Room room
) {
    public static ReviewDto fromEntity(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .type1(review.getType1())
                .type2(review.getType2())
                .type3(review.getType3())
                .type4(review.getType4())
                .type5(review.getType5())
                .room(review.getRoom())
                .build();
    }
}