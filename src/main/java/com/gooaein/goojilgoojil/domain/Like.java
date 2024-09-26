package com.gooaein.goojilgoojil.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "question_id")
    private String questionId;

    @Version // Optimistic Lock 사용
    private int version;

    @Builder
    public Like(User user, String questionId) {
        this.user = user;
        this.questionId = questionId;
    }
}
