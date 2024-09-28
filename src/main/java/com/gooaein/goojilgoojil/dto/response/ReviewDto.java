package com.gooaein.goojilgoojil.dto.response;

import com.gooaein.goojilgoojil.domain.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private List<ReviewQuestionsDto> questions;  // 질문 목록
    private double type1;  // 소수점이 포함된 평균 평점
    private double type2;
    private double type3;
    private double type4;
    private double type5;

    @Builder
    public ReviewDto(List<ReviewQuestionsDto> questions, double type1, double type2, double type3, double type4, double type5) {
        this.questions = questions;
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.type4 = type4;
        this.type5 = type5;
    }

    public static ReviewDto from(List<Review> reviews, List<ReviewQuestionsDto> questions) {
        // 각 리뷰의 평균 계산
        double avgType1 = Double.parseDouble(String.format("%.1f", reviews.stream().mapToInt(Review::getType1).average().orElse(0.0)));
        double avgType2 = Double.parseDouble(String.format("%.1f", reviews.stream().mapToInt(Review::getType2).average().orElse(0.0)));
        double avgType3 = Double.parseDouble(String.format("%.1f", reviews.stream().mapToInt(Review::getType3).average().orElse(0.0)));
        double avgType4 = Double.parseDouble(String.format("%.1f", reviews.stream().mapToInt(Review::getType4).average().orElse(0.0)));
        double avgType5 = Double.parseDouble(String.format("%.1f", reviews.stream().mapToInt(Review::getType5).average().orElse(0.0)));

        return ReviewDto.builder()
                .questions(questions) // 질문 목록을 설정
                .type1(avgType1)
                .type2(avgType2)
                .type3(avgType3)
                .type4(avgType4)
                .type5(avgType5)
                .build();
    }
}
