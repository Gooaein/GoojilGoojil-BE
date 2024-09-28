package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.dto.request.QuestionRequestDto;
import com.gooaein.goojilgoojil.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "질문", description = "질문 관련 API")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/api/v1/users/rooms/{roomId}/questions")
    @Operation(summary = "질문 조회", description = "방에 입장할 때, 입장 직전까지 나왔던 해당 방의 질문들을 조회합니다.")
    public ResponseDto<?> getQuestions(@PathVariable String roomId) {
        return ResponseDto.ok(questionService.getQuestions(roomId));
    }

    @MessageMapping("/rooms/{roomId}/questions")
    @Operation(summary = "질문 보내기", description = "질문을 생성해서 저장하고, 해당 질문이 보내졌음을 다른 유저들이게 알립니다.")
    public ResponseDto<?> sendQuestion(
            @DestinationVariable String roomId,
            @Payload QuestionRequestDto questionRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();
        questionService.sendQuestion(roomId, sessionId, questionRequestDto);
        return ResponseDto.created(null);
    }

    @MessageMapping("/rooms/{roomId}/questions/{questionId}/likes")
    @Operation(summary = "질문 좋아요", description = "질문에 좋아요를 누르면 좋아요 수가 증가하고, 해당 질문에 좋아요를 눌렀음을 다른 유저들에게 알립니다.")
    public ResponseDto<?> likeQuestion(
            @DestinationVariable String roomId,
            @DestinationVariable String questionId,
            SimpMessageHeaderAccessor headerAccessor) {
        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();
        questionService.likeQuestion(roomId, questionId, sessionId);
        return ResponseDto.ok(null);
    }

    @MessageMapping("/rooms/{roomId}/questions/{questionId}/checks")
    @Operation(summary = "질문 답변 처리", description = "질문을 답변하면 해당 질문이 답변되었음을 다른 유저들에게 알립니다.")
    public ResponseDto<?> checkQuestion(
            @DestinationVariable String roomId,
            @DestinationVariable String questionId,
            SimpMessageHeaderAccessor headerAccessor) {
        // WebSocket 세션 ID 가져오기
        String sessionId = headerAccessor.getSessionId();
        questionService.checkQuestion(roomId, questionId, sessionId);
        return ResponseDto.ok(null);
    }

}
