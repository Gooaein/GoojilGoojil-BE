package com.gooaein.goojilgoojil.security.filter;

import com.gooaein.goojilgoojil.dto.type.ERole;
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
        // 로컬 환경인 경우 토큰 검증을 우회합니다.
        if (isLocalEnvironment(request)) {
            // 로컬 환경이면 가짜 인증 객체를 만들어서 사용합니다.
            UsernamePasswordAuthenticationToken mockAuthentication = createMockAuthentication();
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(mockAuthentication);
            SecurityContextHolder.setContext(securityContext);

            filterChain.doFilter(request, response);
            return;
        }

        // 그 외의 환경에서는 기존 JWT 검증 로직을 유지
        String token = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new IllegalArgumentException("Authorization Header Is Not Found!"));
        Claims claims = jwtUtil.validateToken(token);

        JwtUserInfo jwtUserInfo = new JwtUserInfo(
                claims.get(Constants.CLAIM_USER_ID, Long.class),
                ERole.valueOf(claims.get(Constants.CLAIM_USER_ROLE, String.class))
        );

        UsernamePasswordAuthenticationToken unAuthenticatedToken = new UsernamePasswordAuthenticationToken(
                jwtUserInfo, null, null
        );

        UsernamePasswordAuthenticationToken authenticatedToken
                = (UsernamePasswordAuthenticationToken) jwtAuthenticationManager.authenticate(unAuthenticatedToken);

        authenticatedToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticatedToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    // 로컬 환경인 경우 true를 반환하는 메서드
    private boolean isLocalEnvironment(HttpServletRequest request) {
        return "localhost".equals(request.getServerName()) || "127.0.0.1".equals(request.getServerName());
    }

    // 가짜 인증 객체 생성 (로컬 환경에서 사용)
    private UsernamePasswordAuthenticationToken createMockAuthentication() {
        JwtUserInfo mockUser = new JwtUserInfo(1L, ERole.USER);  // 예시로 USER 권한을 가진 가짜 사용자
        return new UsernamePasswordAuthenticationToken(mockUser, null, null);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Constants.NO_NEED_AUTH_URLS.contains(request.getRequestURI())
                || request.getRequestURI().startsWith("/guest");
    }
}

