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

    /* 클라이언트가 입장한 방에 호출 시점까지 나왔던 모든 질문들을 조회한다. 웹소켓에 접근하기 직전에 호출하여,
     * 웹소켓을 통한 실시간 통신 이전까지의 질문들을 불러와, 질문방에서의 질문 데이터 무결성을 보장한다.*/
    @GetMapping("/api/v1/users/rooms/{roomId}/questions")
    @Operation(summary = "질문 조회", description = "방에 입장할 때, 입장 직전까지 나왔던 해당 방의 질문들을 조회합니다.")
    public ResponseDto<?> getQuestions(@PathVariable String roomId) {
        return ResponseDto.ok(questionService.getQuestions(roomId));
    }
    /* 수강자가 웹소켓을 통해 강연자에게 질문을 전송한다. 질문은 모든 수강생 및 강연자가 실시간으로 확인 가능하다.
    *  type(웹소켓 응답 데이터의 타입. 이 경우 question), questionId(질문 아이디), title(질문 제목), content(질문 내용), avatarBase64(질문자의 아바타),
    *  sendTime(질문 전송 시점 -> 무관심 질문 사라지는 시간 계산), likeCount(공감 개수), status(질문 답변 여부)로 이루어진다.
    *  1. 웹소켓의 세션 ID를 가져와 User와 맵핑하여 해당 수강자를 찾아낸다.
    *  2. 수강자로부터 전달받은 데이터와 수강자 데이터를 합쳐 만들어진 Question 객체는 MongoDB에 저장된다.
    *  3. 웹소켓을 통해 현재 질문방을 구독중인 모든 사용자에게 질문과 관련된 데이터를 실시간으로 전송한다.  */
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
    /* 수강자가 웹소켓을 통해 특정 질문에 대해 공감을 표현한다. 이 과정에서는 중복 공감 방지를 위해 Redis가, 동시성 제어를 위해 RabbitMQ가 사용된다.
    *  type(웹소켓 응답 데이터의 타입. 이경우 like), questionId(질문 아이디), title(질문 제목), content(질문 내용), avatarBase64(질문자의 아바타),
    *  sendTime(이 때 sendTime은 갱신된다), likeCount(공감 개수), status(질문 답변 여부)로 이루어진다.
    *  1. 웹소켓의 세션 ID를 가져와 User와 맵핑하여 해당 수강자를 찾아낸다.
    *  2. Redis를 참조하여 해당 사용자의 공감 요청이 중복된 요청인지를 검사한다. 중복이라면 철회한다. 중복이 아니라면 ttl을 설정하여 이후 중복 요청을 방지한다.
    *  3. RabbitMQ에 특정 질문에 대해 공감했음을 Enqueue한다.
    *  4. RabiitMQ는 큐에 있는 데이터를 consume하여, 웹소켓을 통해 질문방에 있는 모든 사용자에게 해당 수강자의 공감과 관련된 데이터를 전송한다.*/
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
    /* 강연자가 웹소켓을 통해 특정 질문에 대해 답변했음을 모든 수강자들에게 전송한다.
    *  type(웹소켓 응답 데이터의 타입. 이 경우 check), questionId(질문 아이디), likeCount(공감 개수), status(질문 답변 여부)로 이루어진다.
    *  1. 웹소켓의 세션 ID를 가져와 User와 맵핑하여 해당 강연자를 찾아낸다.
    *  2. 해당 질문의 답변 여부를 true로 변경한다.
    *  3. 웹소켓을 통해 현재 질문방을 구독중인 모든 사용자에게 해당 질문이 답변되었음을 실시간으로 전송한다.*/
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
