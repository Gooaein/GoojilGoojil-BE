package com.gooaein.goojilgoojil.domain.nosql;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Document(collection = "questions")
public class Question {
    @Id
    private String id; // 질문 아이디
    private final String roomId; // 방 아이디
    private final String title; // 질문 제목
    private final String content; // 질문 내용
    private final String avartarBase64; // 질문자 아바타
    private Integer likeCount; // 좋아요 수
    private String sendTime; // 질문 보낸 시간
    private String status;

    @Builder
    public Question(String roomId, String title, String content, String avartarBase64, Integer likeCount, String status) {
        this.roomId = roomId;
        this.title = title;
        this.content = content;
        this.avartarBase64 = avartarBase64;
        this.likeCount = likeCount;
        this.sendTime = OffsetDateTime.now().toString();
        this.status = status;
    }

    public void like() {
        this.likeCount = this.likeCount + 1;
        this.sendTime = OffsetDateTime.now().toString();
    }

    public void check() {
        this.status = "true";
    }
}

