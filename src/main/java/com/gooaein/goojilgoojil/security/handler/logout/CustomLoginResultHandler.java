package dont.forget.springsecurity.security.handler.logout;

import dont.forget.springsecurity.constant.Constants;
import dont.forget.springsecurity.exception.ErrorCode;
import dont.forget.springsecurity.security.info.AuthenticationResponse;
import dont.forget.springsecurity.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class CustomLoginResultHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication == null){
            log.info("인증 정보가 존재하지 않습니다. authentication is null..");
            AuthenticationResponse.makeFailureResponse(response, ErrorCode.NOT_FOUND_USER);
        }
        CookieUtil.deleteCookie(request, response, Constants.ACCESS_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, Constants.REFRESH_COOKIE_NAME);

        AuthenticationResponse.makeLogoutSuccessResponse(response);
    }
}
