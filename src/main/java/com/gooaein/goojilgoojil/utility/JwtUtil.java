package com.gooaein.goojilgoojil.utility;

import com.gooaein.goojilgoojil.dto.response.JwtTokenDto;
import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil implements InitializingBean {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expire-period}")
    @Getter
    private Integer accessExpiration;

    @Value("${jwt.refresh-token.expire-period}")
    @Getter
    private Integer refreshExpiration;
    private Key key;
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public Claims validateToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String generateToken(Long id, ERole role, Integer expiration, String tokenType){
        Claims claims = Jwts.claims();
        claims.put(Constants.CLAIM_USER_ID, id);
        claims.put("token_type", tokenType);
        if (role != null)
            claims.put(Constants.CLAIM_USER_ROLE, role);


        return Jwts.builder()
                .setHeaderParam(Header.JWT_TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }
    public JwtTokenDto generateTokens(Long id, ERole role){
        return JwtTokenDto.of(
                generateToken(id, role, accessExpiration, "access"),
                generateToken(id, role, refreshExpiration, "refresh")
        );
    }

    // Refresh Token 검증 후 새로운 Access Token 생성
    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = validateToken(refreshToken);  // Refresh Token 검증
        if (isRefreshToken(claims)) {
            Long userId = claims.get(Constants.CLAIM_USER_ID, Long.class);
            ERole role = ERole.valueOf(claims.get(Constants.CLAIM_USER_ROLE, String.class));

            // 새 Access Token 생성
            return generateToken(userId, role, accessExpiration, "access");
        } else {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
    }

    // Refresh Token 확인
    public boolean isRefreshToken(Claims claims) {
        // Refresh Token인지 여부를 구분하는 로직 (custom claim이 필요할 수도 있음)
        // 예: "token_type"이 refresh인 경우에만 Refresh Token으로 간주
        return claims.getExpiration().getTime() - System.currentTimeMillis() > accessExpiration;
    }
}
