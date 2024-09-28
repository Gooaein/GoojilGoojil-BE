package com.gooaein.goojilgoojil.domain;

import com.gooaein.goojilgoojil.dto.request.AuthSignUpDto;
import com.gooaein.goojilgoojil.dto.type.EProvider;
import com.gooaein.goojilgoojil.dto.type.ERole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "users")
public class User {
    /* Default Column */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "serial_id", nullable = false, unique = true)
    private String serialId;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProvider provider;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /* User Status */
    @Column(name = "is_login", columnDefinition = "TINYINT(1)")
    private Boolean isLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "session_id")
    private String sessionId;

    @Builder
    public User(String serialId, String password, EProvider provider, ERole role, String nickname) {
        this.serialId = serialId;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isLogin = false;
        this.nickname = nickname;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void updateLoginStatus(Boolean isLogin) {
        this.isLogin = isLogin;
    }

    public static User signUp(AuthSignUpDto authSignUpDto, String encodedPassword) {
        return User.builder()
                .serialId(authSignUpDto.serialId())
                .password(encodedPassword)
                .provider(EProvider.DEFAULT)
                .role(ERole.USER)
                .build();
    }

    public void updateSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static User signUp(String serialId, EProvider provider, String nickname) {
        return User.builder()
                .serialId(serialId)
                .provider(provider)
                .password(null)
                .nickname(nickname)
                .role(ERole.USER)
                .build();
    }
}
