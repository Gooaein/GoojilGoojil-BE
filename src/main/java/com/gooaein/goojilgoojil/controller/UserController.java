package com.gooaein.goojilgoojil.controller;

import com.gooaein.goojilgoojil.annotation.UserId;
import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저(유학생, 점주 공통)", description = "유저 관련 API")
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "본인 정보 조회(홈화면 조회)", description = "본인 정보를 조회합니다.")
    public ResponseDto<?> readUsers(
            @UserId Long userId
    ) {
        return ResponseDto.ok(userService.readUser(userId));
    }
}
