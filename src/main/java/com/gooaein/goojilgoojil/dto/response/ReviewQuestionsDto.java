package com.gooaein.goojilgoojil.dto.response;

import com.gooaein.goojilgoojil.domain.nosql.Question;
import lombok.Builder;

@Builder
public record ReviewQuestionsDto(
        String title,
        String content
) {
    public static ReviewQuestionsDto fromEntity(Question question) {
        return ReviewQuestionsDto.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .build();
    }
}