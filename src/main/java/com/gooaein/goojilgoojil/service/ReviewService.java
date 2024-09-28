package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Review;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.nosql.Question;
import com.gooaein.goojilgoojil.dto.response.ReviewCreateDto;
import com.gooaein.goojilgoojil.dto.response.ReviewDto;
import com.gooaein.goojilgoojil.dto.response.ReviewQuestionsDto;
import com.gooaein.goojilgoojil.repository.QuestionRepository;
import com.gooaein.goojilgoojil.repository.ReviewRepository;
import com.gooaein.goojilgoojil.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final QuestionRepository questionRepository;
    @Transactional
    public void createReview(Long roomId, ReviewCreateDto reviewCreateDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        Review review = reviewCreateDto.toEntity(room);
        reviewRepository.save(review);
    }

    public ReviewDto getReviewByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        List<Review> reviews = reviewRepository.findAllByRoom(room);
        if (reviews.isEmpty()) {
            throw new IllegalArgumentException("해당 방에 대한 리뷰가 없습니다.");
        }
        List<ReviewQuestionsDto> questions = questionRepository.findAllByRoomId(roomId.toString()).stream()
                .map(ReviewQuestionsDto::fromEntity)
                .toList();

        return ReviewDto.from(reviews, questions);
    }
}


