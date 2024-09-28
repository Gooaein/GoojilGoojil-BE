package com.gooaein.goojilgoojil.service.rabbitMQ;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Like;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.domain.nosql.Question;
import com.gooaein.goojilgoojil.dto.request.LikeRequestDto;
import com.gooaein.goojilgoojil.dto.response.QuestionResponseDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.GuestRepository;
import com.gooaein.goojilgoojil.repository.LikeRepository;
import com.gooaein.goojilgoojil.repository.QuestionRepository;
import com.gooaein.goojilgoojil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeConsumerService {
    private final QuestionRepository questionRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /* 질문 공감에 대한 처리 로직.
    *  Queue에서 consume하여, 특정 질문에 대한 공감(like)를 생성하고 저장한다.
    *  또한, 웹소켓에 해당 질문이 공감되었다는 메세지를 전송함으로써, 모든 사용자들이 실시간으로 그 사실을 알게 한다. */
    @RabbitListener(queues = "likeQueue")
    @Transactional
    public void consumeLike(LikeRequestDto likeRequestDto) {
        String questionId = likeRequestDto.questionId();
        String roomId = likeRequestDto.roomId();
        Long userId = likeRequestDto.userId();

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_QUESTION));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Guest guest = guestRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

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
                .avatarBase64(guest.getAvatarBase64())
                .sendTime(question.getSendTime())
                .likeCount(question.getLikeCount())
                .status(question.getStatus())
                .build();

        messagingTemplate.convertAndSend("/subscribe/rooms/" + roomId, responseDto);
    }
}

