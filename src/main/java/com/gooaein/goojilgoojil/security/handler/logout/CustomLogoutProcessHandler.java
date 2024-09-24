package com.gooaein.goojilgoojil.security.handler.logout;

import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.UserRepository;
import com.gooaein.goojilgoojil.security.info.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomLogoutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null){
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        userRepository.updateRefreshTokenAndLoginStatus(userPrincipal.getUserId(), null, false);
    }
}
