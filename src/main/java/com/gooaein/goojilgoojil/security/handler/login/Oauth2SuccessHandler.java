package com.gooaein.goojilgoojil.security.handler.login;

import com.gooaein.goojilgoojil.dto.response.JwtTokenDto;
import com.gooaein.goojilgoojil.repository.UserRepository;
import com.gooaein.goojilgoojil.security.info.AuthenticationResponse;
import com.gooaein.goojilgoojil.security.info.UserPrincipal;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(principal.getUserId(), principal.getRole());

        userRepository.updateRefreshTokenAndLoginStatus(principal.getUserId(), jwtTokenDto.refreshToken(), true);

        AuthenticationResponse.makeLoginSuccessResponse(response, jwtTokenDto, jwtUtil.getRefreshExpiration());

        response.sendRedirect("http://localhost:3000");
    }
}
