package com.gooaein.goojilgoojil.security.filter;

import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.security.provider.JwtAuthenticationManager;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.security.info.JwtUserInfo;
import com.gooaein.goojilgoojil.utility.HeaderUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new IllegalArgumentException("Authorization Header Is Not Found!"));
        Claims claims = jwtUtil.validateToken(token);

        if (request.getRequestURI().equals("/api/v1/auth/reissue")) { // access 재발행 로직인 경우
            if (!jwtUtil.isRefreshToken(claims)) { // refresh token이 아닌 경우
                throw new CommonException(ErrorCode.TOKEN_TYPE_ERROR);
            }
        } else if (jwtUtil.isRefreshToken(claims)) { // 일반적인 API 호출인 경우일 때 refresh token인 경우
            throw new CommonException(ErrorCode.TOKEN_TYPE_ERROR);
        }

        JwtUserInfo jwtUserInfo = new JwtUserInfo(
                claims.get(Constants.CLAIM_USER_ID, Long.class),
                ERole.valueOf(claims.get(Constants.CLAIM_USER_ROLE, String.class))
        );

        // 인증 받지 않은 인증용 객체
        UsernamePasswordAuthenticationToken unAuthenticatedToken = new UsernamePasswordAuthenticationToken(
                jwtUserInfo, null, null
        );

        // 인증 받은 후의 인증 객체
        UsernamePasswordAuthenticationToken authenticatedToken
                = (UsernamePasswordAuthenticationToken) jwtAuthenticationManager.authenticate(unAuthenticatedToken);

        // 사용자의 IP등 세부 정보 인증 정보에 추가
        authenticatedToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticatedToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 URL 목록에 포함되는지 확인
        return Constants.NO_NEED_FILTER_URLS.stream()
                .anyMatch(excludePattern -> requestURI.matches(excludePattern.replace("**", ".*")));
    }
}
