package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.annotation.UserId;
import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.global.ResponseDto;
import com.gooaein.goojilgoojil.dto.request.AuthSignUpDto;
import com.gooaein.goojilgoojil.dto.request.OauthLoginDto;
import com.gooaein.goojilgoojil.dto.response.JwtTokenDto;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.UserRepository;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUp(AuthSignUpDto authSignUpDto) {
        userRepository.save(
                User.signUp(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()))
        );
    }

    @Transactional
    public JwtTokenDto reissue(Long userId, String refreshToken) {
        User user = userRepository.findByIdAndRefreshTokenAndIsLogin(userId, refreshToken, true)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_LOGIN_USER));

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }

    public boolean checkDuplicate(String serialId) {
        return userRepository.existsBySerialId(serialId);
    }
}
