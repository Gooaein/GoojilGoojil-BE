package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Like;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.domain.nosql.Question;
import com.gooaein.goojilgoojil.dto.request.QuestionRequestDto;
import com.gooaein.goojilgoojil.dto.response.QuestionResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LikeRepository likeRepository;

    public List<QuestionResponseDto> getQuestions(String roomId) {
        return questionRepository.findAllByRoomId(roomId).stream()
                .map(question -> QuestionResponseDto.builder()
                        .questionId(question.getId())
                        .type("question")
                        .title(question.getTitle())
                        .content(question.getContent())
                        .avartarBase64(question.getAvartarBase64())
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
                        .avartarBase64(guest.getAvartarBase64())
                        .likeCount("0")
                        .status("false")
                        .build()
        );

        QuestionResponseDto responseDto = QuestionResponseDto.builder()
                .type(questionRequestDto.type())
                .questionId(question.getId())
                .title(questionRequestDto.title())
                .content(questionRequestDto.content())
                .avartarBase64(guest.getAvartarBase64())
                .sendTime(OffsetDateTime.now().toString())
                .likeCount(question.getLikeCount())
                .status(question.getStatus())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }

    @Transactional
    public void likeQuestion(String roomId, String questionId, String sessionId) {
        User user = userRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_QUESTION));
        if (likeRepository.findByUserIdAndQuestionId(user.getId(), questionId).isPresent()) {
            throw new CommonException(ErrorCode.ALREADY_LIKED_QUESTION);
        }
        likeRepository.save(Like.builder()
                .user(user)
                .questionId(questionId)
                .build()
        );
        question.like();
        questionRepository.save(question);
        QuestionResponseDto responseDto = QuestionResponseDto.builder()
                .type("like")
                .questionId(question.getId())
                .avartarBase64(guest.getAvartarBase64())
                .sendTime(question.getSendTime())
                .likeCount(question.getLikeCount())
                .status(question.getStatus())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
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
