package com.gooaein.goojilgoojil.security.config;

import com.gooaein.goojilgoojil.security.handler.exception.CustomAccessDeniedHandler;
import com.gooaein.goojilgoojil.security.handler.exception.CustomAuthenticationEntryPointHandler;
import com.gooaein.goojilgoojil.security.handler.login.DefaultFailureHandler;
import com.gooaein.goojilgoojil.security.handler.login.DefaultSuccessHandler;
import com.gooaein.goojilgoojil.security.handler.login.Oauth2FailureHandler;
import com.gooaein.goojilgoojil.security.handler.login.Oauth2SuccessHandler;
import com.gooaein.goojilgoojil.security.handler.logout.CustomLoginResultHandler;
import com.gooaein.goojilgoojil.security.handler.logout.CustomLogoutProcessHandler;
import com.gooaein.goojilgoojil.security.provider.JwtAuthenticationManager;
import com.gooaein.goojilgoojil.security.service.CustomOauth2UserDetailService;
import com.gooaein.goojilgoojil.utility.JwtUtil;
import com.gooaein.goojilgoojil.constants.Constants;
import com.gooaein.goojilgoojil.security.filter.GlobalLoggerFilter;
import com.gooaein.goojilgoojil.security.filter.JwtAuthenticationFilter;
import com.gooaein.goojilgoojil.security.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DefaultSuccessHandler defaultSuccessHandler;
    private final DefaultFailureHandler defaultFailureHandler;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;
    private final CustomOauth2UserDetailService customOauth2UserDetailService;
    private final CustomLogoutProcessHandler customLogoutProcessHandler;
    private final CustomLoginResultHandler customLoginResultHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtUtil jwtUtil;

    @Bean
    protected SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // csrf 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 기본 인증 방식 해제
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안하고 상태가 없는 방식으로 인증 = JWT 사용
                )

                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers(Constants.NO_NEED_AUTH_URLS.toArray(String[]::new)).permitAll()
                                .requestMatchers(Constants.USERS_URLS.toArray(String[]::new)).hasRole("USER")
                                .anyRequest().authenticated()
                )

                .formLogin(configurer ->
                        configurer
                                .loginPage("/login")
                                .loginProcessingUrl("/api/v1/auth/login")
                                .usernameParameter("serial_id")
                                .passwordParameter("password")
                                .successHandler(defaultSuccessHandler)
                                .failureHandler(defaultFailureHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                        .userInfoEndpoint(it -> it.userService(customOauth2UserDetailService))
                )
                .logout(configurer ->
                        configurer
                                .logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(customLogoutProcessHandler)
                                .logoutSuccessHandler(customLoginResultHandler)
                )
                .exceptionHandling(configurer ->
                        configurer
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, jwtAuthenticationManager),
                        LogoutFilter.class)
                .addFilterBefore(
                        new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class)
                .addFilterBefore(
                        new GlobalLoggerFilter(),
                        JwtExceptionFilter.class)

                .getOrBuild();
    }
}
