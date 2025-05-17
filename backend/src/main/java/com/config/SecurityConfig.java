package com.config;

import com.security.JwtAuthenticationFilter;
import com.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * - JwtAuthenticationFilter를 SecurityFilterChain에 등록
 * - 인증/인가 정책 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * AuthenticationManager 빈 등록
     * - Spring Security에서 로그인 인증 시 사용
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain 설정
     *
     * [기능 요약]
     * - CSRF 비활성화 (JWT 기반 인증에는 필요 없음)
     * - 특정 경로는 인증 없이 접근 허용
     * - 나머지 요청은 인증 필요
     * - JWT 필터 등록
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화(JWT는 세션을 사용하지 않음)
                .csrf(AbstractHttpConfigurer::disable)
                // 경로별 인가 정책 설정
                .authorizeHttpRequests(auth -> auth
                        //로그인, 회원가입 등 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        //swagger허용
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**","/v3/api-docs/**").permitAll()
                        //그 이외 요청은 인증 필수
                        .anyRequest().authenticated()
                )

                // JWT필터 등록(기존 UsernamePasswordAuthenticationFilter 앞에 삽입)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
