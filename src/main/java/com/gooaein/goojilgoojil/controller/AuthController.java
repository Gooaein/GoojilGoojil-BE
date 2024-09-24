package com.goormthon.goojilgoojil.controller;

import com.goormthon.goojilgoojil.annotation.UserId;
import com.goormthon.goojilgoojil.constants.Constants;
import com.goormthon.goojilgoojil.dto.global.ResponseDto;
import com.goormthon.goojilgoojil.dto.request.AuthSignUpDto;
import com.goormthon.goojilgoojil.dto.response.JwtTokenDto;
import com.goormthon.goojilgoojil.exception.CommonException;
import com.goormthon.goojilgoojil.exception.ErrorCode;
import com.goormthon.goojilgoojil.service.AuthService;
import com.goormthon.goojilgoojil.utility.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원가입/로그인", description = "인증 관련 API")
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/auth/id-duplicate")
    @Operation(summary = "아이디 중복 확인", description = "아이디 중복을 확인합니다.")
    public ResponseDto<?> checkDuplicate(
            @RequestParam(value = "serial_id") String serialId
    ) {
        return ResponseDto.ok(authService.checkDuplicate(serialId));
    }

    @PostMapping("/auth/sign-up")
    @Operation(summary = "Default 회원가입", description = "Default 회원가입을 진행합니다.")
    public void signUp(
            @RequestBody @Valid AuthSignUpDto authSignUpDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.signUp(authSignUpDto, request, response);
    }

    @PostMapping("/auth/reissue")
    @Operation(summary = "Access 토큰 재발급", description = "Access 토큰을 재발급합니다.")
    public ResponseDto<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response,
            @UserId Long userId) {
        String refreshToken = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new CommonException(ErrorCode.MISSING_REQUEST_HEADER));

        JwtTokenDto jwtTokenDto = authService.reissue(userId, refreshToken);

        return ResponseDto.ok(jwtTokenDto);
    }

}
