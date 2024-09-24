package dont.forget.springsecurity.security.handler.logout;

import dont.forget.springsecurity.exception.CommonException;
import dont.forget.springsecurity.exception.ErrorCode;
import dont.forget.springsecurity.repository.UserRepository;
import dont.forget.springsecurity.security.info.UserPrincipal;
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
