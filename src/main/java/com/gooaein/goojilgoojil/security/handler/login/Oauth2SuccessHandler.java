package dont.forget.springsecurity.security.handler.login;

import dont.forget.springsecurity.dto.response.JwtTokenDto;
import dont.forget.springsecurity.repository.UserRepository;
import dont.forget.springsecurity.security.info.AuthenticationResponse;
import dont.forget.springsecurity.security.info.UserPrincipal;
import dont.forget.springsecurity.util.CookieUtil;
import dont.forget.springsecurity.util.JwtUtil;
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
