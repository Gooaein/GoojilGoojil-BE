package com.gooaein.goojilgoojil.security.info;

import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.dto.global.ExceptionDto;
import com.gooaein.goojilgoojil.dto.response.JwtTokenDto;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.utility.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationResponse {
    public static void makeLoginSuccessResponse(HttpServletResponse response, JwtTokenDto jwtTokenDto, Integer refreshExpiration){
        CookieUtil.addCookie(response, Constants.ACCESS_COOKIE_NAME, jwtTokenDto.accessToken());
        CookieUtil.addSecureCookie(response, Constants.REFRESH_COOKIE_NAME, jwtTokenDto.refreshToken(), refreshExpiration);
    }
    public static void makeLogoutSuccessResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> body = new HashMap<>();
        body.put("success", "true");
        body.put("data", null);
        body.put("error", null);

        response.getWriter().write(JSONValue.toJSONString(body));
    }
    public static void makeFailureResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (errorCode != null) {
            response.setStatus(errorCode.getHttpStatus().value());
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());  // 기본값 설정
        }

        Map<String, Object> body= new HashMap<>();
        body.put("success", false);
        body.put("data", null);
        body.put("error", ExceptionDto.of(errorCode));

        response.getWriter().write(JSONValue.toJSONString(body));

    }
}
