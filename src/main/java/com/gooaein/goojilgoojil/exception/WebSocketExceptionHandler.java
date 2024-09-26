package com.gooaein.goojilgoojil.exception;

import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class WebSocketExceptionHandler {

    // CommonException 예외 처리
    @MessageExceptionHandler(CommonException.class)
    @SendToUser("/queue/errors")  // 에러 메시지를 클라이언트에게 전송
    public ResponseDto<?> handleCommonException(CommonException e) {
        log.error("handleCommonException() in WebSocketExceptionHandler throw CommonException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // Argument 타입이 일치하지 않을 때 발생하는 예외
    @MessageExceptionHandler(MethodArgumentTypeMismatchException.class)
    @SendToUser("/queue/errors")
    public ResponseDto<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException() in WebSocketExceptionHandler throw MethodArgumentTypeMismatchException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 그 외의 모든 예외 처리
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ResponseDto<?> handleException(Exception e) {
        log.error("handleException() in WebSocketExceptionHandler throw Exception : {}", e.getMessage());
        e.printStackTrace();
        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
