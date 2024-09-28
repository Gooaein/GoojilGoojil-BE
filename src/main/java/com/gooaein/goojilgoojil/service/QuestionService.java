package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Like;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.domain.nosql.Question;
import com.gooaein.goojilgoojil.dto.request.LikeRequestDto;
import com.gooaein.goojilgoojil.dto.request.QuestionRequestDto;
import com.gooaein.goojilgoojil.dto.response.QuestionResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final QuestionRepository questionRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<QuestionResponseDto> getQuestions(String roomId) {
        return questionRepository.findAllByRoomId(roomId).stream()
                .map(question -> QuestionResponseDto.builder()
                        .questionId(question.getId())
                        .type("question")
                        .title(question.getTitle())
                        .content(question.getContent())
                        .avatarBase64(question.getAvatarBase64())
                        .sendTime(question.getSendTime())
                        .likeCount(question.getLikeCount())
                        .status(question.getStatus())
                        .build())
                .toList();
    }
    @Transactional
    public void sendQuestion(String roomId, String sessionId, QuestionRequestDto questionRequestDto) {

        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Question question = questionRepository.save(
                Question.builder()
                        .roomId(roomId)
                        .title(questionRequestDto.title())
                        .content(questionRequestDto.content())
                        .avatarBase64(guest.getAvatarBase64())
                        .likeCount(0)
                        .status("false")
                        .build()
        );

        QuestionResponseDto responseDto = QuestionResponseDto.builder()
                .type("question")
                .questionId(question.getId())
                .title(questionRequestDto.title())
                .content(questionRequestDto.content())
                .avatarBase64(guest.getAvatarBase64())
                .sendTime(OffsetDateTime.now().toString())
                .likeCount(question.getLikeCount())
                .status(question.getStatus())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }

    public void likeQuestion(String roomId, String questionId, String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // Redis 중복 공감 체크
        String redisKey = "like:" + user.getId() + ":" + questionId;
        Boolean isDuplicate = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);

        // 이미 공감한 경우 중단
        if (Boolean.FALSE.equals(isDuplicate)) {
            throw new CommonException(ErrorCode.ALREADY_LIKED_QUESTION);
        }

        rabbitTemplate.convertAndSend("likeQueue", LikeRequestDto.builder()
                .roomId(roomId)
                .questionId(questionId)
                .userId(user.getId())
                .build());
    }

    @Transactional
    public void checkQuestion(String roomId, String questionId, String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (guestRepository.findById(user.getId()).isPresent()) {
            throw new CommonException(ErrorCode.CANNOT_CHECK_QUESTION);
        }
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_QUESTION));
        question.check();
        questionRepository.save(question);
        QuestionResponseDto responseDto = QuestionResponseDto.builder()
                .type("check")
                .questionId(question.getId())
                .likeCount(question.getLikeCount())
                .status(question.getStatus())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }
}
